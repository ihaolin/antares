package me.hao0.antares.store.dao.impl;

import me.hao0.antares.common.model.App;
import me.hao0.antares.store.dao.AppDao;
import me.hao0.antares.store.support.RedisKeys;
import org.springframework.stereotype.Repository;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Repository
public class AppDaoImpl extends RedisDao<App> implements AppDao {

    @Override
    public Boolean index(App app) {
        redis.opsForHash().put(RedisKeys.APP_INDEX_NAMES, app.getAppName(), app.getId());
        return Boolean.TRUE;
    }

    @Override
    public Boolean unIndex(App app) {
        redis.opsForHash().delete(RedisKeys.APP_INDEX_NAMES, app.getAppName());
        return Boolean.TRUE;
    }

    @Override
    public App findByName(String name) {
        Object idObj = redis.opsForHash().get(RedisKeys.APP_INDEX_NAMES, name);
        return idObj == null ? null : findById(Long.valueOf(idObj.toString()));
    }
}
