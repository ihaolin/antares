package me.hao0.antares.store.dao.impl;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import me.hao0.antares.common.anno.RedisModel;
import me.hao0.antares.common.model.Model;
import me.hao0.antares.common.util.CollectionUtil;
import me.hao0.antares.common.util.Names;
import me.hao0.antares.store.dao.BaseDao;
import me.hao0.antares.store.support.RedisIds;
import me.hao0.antares.store.support.RedisKeys;
import me.hao0.antares.store.util.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@SuppressWarnings("unchecked")
public class RedisDao<T extends Model> implements BaseDao<T> {

    protected static final Integer DELETE_BATCH_SIZE = 100;

    @SuppressWarnings("unchecked")
    protected final Class<?> genericClazz =
            (Class<T>)((ParameterizedType)getClass()
                    .getGenericSuperclass()).getActualTypeArguments()[0];

    protected final String OBJECT_PREFIX = getObjectPrefix();

    private String getObjectPrefix() {

        RedisModel redisModel = genericClazz.getAnnotation(RedisModel.class);
        if (redisModel != null){
            return redisModel.prefix();
        }

        return Names.toUnderScore(genericClazz.getSimpleName()) + "s";
    }

    protected final String IDS_KEY = RedisKeys.keyOfIds(OBJECT_PREFIX);

    protected final String IDG_KEY = RedisKeys.keyOfIdGenerator(OBJECT_PREFIX);

    @Autowired
    protected StringRedisTemplate redis;

    @Autowired
    private RedisIds ids;

    @Override
    public Boolean save(Model m) {

        Date now = new Date();
        boolean isCreated = false;
        if (m.getId() == null){
            // create
            isCreated = true;
            m.setId(ids.generate(IDG_KEY));
            m.setCtime(now);
        }
        m.setUtime(now);

        // save object
        String objKey = objectKey(m.getId());
        Map<?, ?> objMap = Maps.toMap(m);
        redis.opsForHash().putAll(objKey, objMap);

        // bind ids if create
        if (isCreated){
            redis.opsForList().leftPush(IDS_KEY, m.getId().toString());
        }

        return Boolean.TRUE;
    }

    @Override
    public T findById(Long id) {
        String objKey = objectKey(id);
        Map objectMap = redis.opsForHash().entries(objKey);
        if (objectMap == null || objectMap.isEmpty()){
            return null;
        }
        return (T)Maps.fromMap(objectMap, genericClazz);
    }

    @Override
    public Boolean delete(Long id) {

        // delete id in ids
        redis.opsForList().remove(IDS_KEY, 1, String.valueOf(id));

        // delete object
        String objKey = objectKey(id);
        redis.delete(objKey);

        return Boolean.TRUE;
    }

    protected void deleteBatch(String listKey) {
        int offset = 0;
        List<String> strIds;
        // delete all instances
        while (true){
            strIds = listStr(listKey, offset, DELETE_BATCH_SIZE);
            if (CollectionUtil.isNullOrEmpty(strIds)){
                break;
            }
            for (String id : strIds){
                delete(Long.valueOf(id));
            }
            offset += DELETE_BATCH_SIZE;
        }
    }

    @Override
    public List<T> findByIds(final List<Long> ids) {
        List<T> objs = Lists.newArrayListWithExpectedSize(ids.size());
        // iterate instead of the pipeline
        // so that support the redis proxy or cluster environment
        T t;
        for (Long id : ids){
            t = findById(id);
            if (t != null){
                objs.add(t);
            }
        }
        return objs;
    }

    @Override
    public Long findMaxId() {
        return findMaxId(IDS_KEY);
    }

    @Override
    public Long findMaxId(String listKey) {
        String idStr = redis.opsForList().index(listKey, 0);
        if (Strings.isNullOrEmpty(idStr)){
            return null;
        }
        return Long.valueOf(idStr);
    }

    @Override
    public T findLatest() {
        Long maxId = findMaxId();
        if (maxId == null){
            return null;
        }
        return findById(maxId);
    }

    @Override
    public Long count() {
        return count(IDS_KEY);
    }

    @Override
    public Long count(String listKey) {
        return redis.opsForList().size(listKey);
    }

    @Override
    public List<T> list(Integer offset, Integer limit) {
        return list(IDS_KEY, offset, limit);
    }

    @Override
    public List<String> listStr(String listKey, Integer offset, Integer limit) {
        return redis.opsForList().range(listKey, offset, offset + limit - 1);
    }

    @Override
    public List<T> list(String idsKey, Integer offset, Integer limit) {
        final List<String> ids = redis.opsForList().range(idsKey, offset, offset + limit - 1);
        if (ids == null || ids.isEmpty()){
            return Collections.emptyList();
        }

        List<Long> idsLong = Lists.transform(ids, new Function<String, Long>() {
            @Override
            public Long apply(String s) {
                return Long.valueOf(s);
            }
        });

        return findByIds(idsLong);
    }

    @Override
    public List<Long> listIds(String listKey, Integer offset, Integer limit) {
        List<String> listStr = listStr(listKey, offset, limit);
        if (CollectionUtil.isNullOrEmpty(listStr)){
            return Collections.emptyList();
        }

        return Lists.transform(listStr, new Function<String, Long>() {
            @Override
            public Long apply(String idStr) {
                return Long.valueOf(idStr);
            }
        });
    }

    @Override
    public Integer getIntegerField(Long id, String fieldName) {
        String objectKey = objectKey(id);
        Object fieldValue = redis.opsForHash().get(objectKey, fieldName);
        return fieldValue == null ? null : Integer.valueOf(fieldValue.toString());
    }

    @Override
    public Boolean updateField(Long id, String fieldName, Object fieldValue) {
        String objectKey = objectKey(id);
        redis.opsForHash().put(objectKey, fieldName, fieldValue);
        return Boolean.TRUE;
    }

    /**
     * Default use id as unique key
     */
    protected String objectKey(Object id) {
        return RedisKeys.format(OBJECT_PREFIX, id.toString());
    }
}
