package me.hao0.antares.store.dao.impl;

import me.hao0.antares.common.model.JobAssign;
import me.hao0.antares.store.dao.JobAssignDao;
import me.hao0.antares.store.support.RedisKeys;
import org.springframework.stereotype.Repository;
import java.util.Set;

/**
 * The job assign dao
 * Author : haolin
 * Email  : haolin.h0@gmail.com
 */
@Repository
public class JobAssignDaoImpl extends RedisDao<JobAssign> implements JobAssignDao {

    @Override
    public Boolean addAssign(Long jobId, String... clientIps) {
        return redis.opsForSet().add(RedisKeys.keyOfJobAssigns(jobId), clientIps) > 0L;
    }

    @Override
    public Boolean removeAssign(Long jobId, Object... clientIps) {
        return redis.opsForSet().remove(RedisKeys.keyOfJobAssigns(jobId), clientIps) > 0L;
    }

    @Override
    public Boolean isAssigned(Long jobId, String ip) {

        String jobAssignsKey = RedisKeys.keyOfJobAssigns(jobId);
        if (!redis.hasKey(jobAssignsKey)){
            return Boolean.TRUE;
        }

        return redis.opsForSet().isMember(jobAssignsKey, ip);
    }

    @Override
    public Set<String> listAssigns(Long jobId) {
        return redis.opsForSet().members(RedisKeys.keyOfJobAssigns(jobId));
    }

    @Override
    public Boolean cleanAssign(Long jobId) {
        redis.delete(RedisKeys.keyOfJobAssigns(jobId));
        return Boolean.TRUE;
    }
}
