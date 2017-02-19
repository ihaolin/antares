package me.hao0.antares.store.dao.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import me.hao0.antares.common.model.JobInstanceShard;
import me.hao0.antares.common.model.enums.JobInstanceShardStatus;
import me.hao0.antares.common.util.CollectionUtil;
import me.hao0.antares.store.dao.JobInstanceShardDao;
import me.hao0.antares.store.support.RedisKeys;
import org.springframework.stereotype.Repository;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Repository
public class JobInstanceShardDaoImpl extends RedisDao<JobInstanceShard> implements JobInstanceShardDao {

    @Override
    public Boolean bindInstance(Long instanceId, Long progressId) {
        String jobInstanceProgressesKey = RedisKeys.keyOfJobInstanceShards(instanceId);
        return redis.opsForList()
                .leftPush(jobInstanceProgressesKey, progressId.toString()) > 0;
    }

    @Override
    public Boolean unbindInstance(Long instanceId, Long progressId) {
        String jobInstanceProgressesKey = RedisKeys.keyOfJobInstanceShards(instanceId);
        return redis.opsForList()
                .remove(jobInstanceProgressesKey, 1, progressId.toString()) > 0;
    }

    @Override
    public Long countByInstanceId(Long instanceId) {
        return redis.opsForList().size(RedisKeys.keyOfJobInstanceShards(instanceId));
    }

    @Override
    public List<JobInstanceShard> listByInstanceId(Long instanceId, Integer offset, Integer limit) {
        return list(RedisKeys.keyOfJobInstanceShards(instanceId), offset, limit);
    }

    @Override
    public Boolean deleteByInstanceId(Long instanceId) {
        String shardsKey = RedisKeys.keyOfJobInstanceShards(instanceId);
        deleteBatch(shardsKey);

        return Boolean.TRUE;
    }

    @Override
    public Boolean createNewShardsSet(Long instanceId, List<Long> shardIds) {
        String shardsNewSetKey = RedisKeys.keyOfJobInstanceStatusShards(instanceId, JobInstanceShardStatus.NEW);
        String[] shardIdsStr = new String[shardIds.size()];
        for (int i=0; i<shardIds.size(); i++){
            shardIdsStr[i] = String.valueOf(shardIds.get(i));
        }
        return redis.opsForSet().add(shardsNewSetKey, shardIdsStr) > 0;
    }

    @Override
    public Boolean returnShard2NewShardsSet(Long instanceId, Long shardId) {
        String shardsKey = RedisKeys.keyOfJobInstanceStatusShards(instanceId, JobInstanceShardStatus.NEW);
        return redis.opsForSet().add(shardsKey, String.valueOf(shardId)) > 0;
    }

    @Override
    public Long pullShardFromNewShardsSet(Long instanceId) {
        String shardsKey = RedisKeys.keyOfJobInstanceStatusShards(instanceId, JobInstanceShardStatus.NEW);
        String shardId = redis.opsForSet().pop(shardsKey);
        return Strings.isNullOrEmpty(shardId) ? null : Long.valueOf(shardId);
    }

    @Override
    public Boolean addShard2StatusSet(Long instanceId, Long shardId, JobInstanceShardStatus status) {
        String statusShardsSet = RedisKeys.keyOfJobInstanceStatusShards(instanceId, status);
        return redis.opsForSet().add(statusShardsSet, String.valueOf(shardId)) > 0;
    }

    @Override
    public Boolean removeShardFromStatusSet(Long instanceId, Long shardId, JobInstanceShardStatus status) {
        String statusShardsSet = RedisKeys.keyOfJobInstanceStatusShards(instanceId, status);
        return redis.opsForSet().remove(statusShardsSet, String.valueOf(shardId)) > 0;
    }

    @Override
    public Boolean addShard2ClientRunningSet(String client, Long shardId) {
        String clientShardsKey = RedisKeys.keyOfClientRunningShards(client);
        return redis.opsForSet().add(clientShardsKey, shardId.toString()) > 0;
    }

    @Override
    public List<Long> getClientRunningShards(String client) {

        String clientShardsKey = RedisKeys.keyOfClientRunningShards(client);

        Set<String> shardIdsStr = redis.opsForSet().members(clientShardsKey);
        if (CollectionUtil.isNullOrEmpty(shardIdsStr)){
            return Collections.emptyList();
        }

        List<Long> shardIds = Lists.newArrayListWithExpectedSize(shardIdsStr.size());
        for (String idStr : shardIdsStr){
            shardIds.add(Long.valueOf(idStr));
        }

        return shardIds;
    }

    @Override
    public Boolean removeShardFromClientRunningShards(String client, Long shardId) {
        String clientShardsKey = RedisKeys.keyOfClientRunningShards(client);
        return redis.opsForSet().remove(clientShardsKey, shardId.toString()) > 0;
    }

    @Override
    public Integer getJobInstanceTotalShardCount(Long instanceId) {
        String jobInstanceShardsKey = RedisKeys.keyOfJobInstanceShards(instanceId);
        return redis.opsForList().size(jobInstanceShardsKey).intValue();
    }

    @Override
    public Integer getJobInstanceStatusShardCount(Long instanceId, JobInstanceShardStatus status) {
        String jobInstanceStatusShardsKey = RedisKeys.keyOfJobInstanceStatusShards(instanceId, status);
        return redis.opsForSet().size(jobInstanceStatusShardsKey).intValue();
    }
}
