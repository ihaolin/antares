package me.hao0.antares.store.dao.impl;

import me.hao0.antares.common.model.Job;
import me.hao0.antares.store.dao.JobDao;
import me.hao0.antares.store.support.RedisKeys;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * The job dao implements
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Repository
public class JobDaoImpl extends RedisDao<Job> implements JobDao {

    @Override
    public Boolean bindApp(Long appId, Long jobId) {
        String appJobsKey = RedisKeys.keyOfAppJobs(appId);
        return redis.opsForList()
                .leftPush(appJobsKey, jobId.toString()) > 0L;
    }

    @Override
    public Boolean unbindApp(Long appId, Long jobId) {
        String appJobsKey = RedisKeys.keyOfAppJobs(appId);
        return redis.opsForList().remove(appJobsKey, 1, jobId.toString()) > 0L;
    }

    @Override
    public Boolean indexJobClass(Long appId, Long jobId, String clazz) {
        String appJobClassesKey = RedisKeys.keyOfAppJobClasses(appId);
        redis.opsForHash().put(appJobClassesKey, clazz, jobId.toString());
        return Boolean.TRUE;
    }

    @Override
    public Job findByJobClass(Long appId, String clazz) {
        Long jobId = findIdByJobClass(appId, clazz);
        return jobId == null ? null : findById(jobId);
    }

    @Override
    public Long findIdByJobClass(Long appId, String clazz) {
        String appJobClassesKey = RedisKeys.keyOfAppJobClasses(appId);
        Object jobIdStr = redis.opsForHash().get(appJobClassesKey, clazz);
        if (jobIdStr == null) {
            return null;
        }
        return Long.valueOf(jobIdStr.toString());
    }

    @Override
    public Boolean unIndexJobClass(Long appId, String clazz) {
        String appJobClassesKey = RedisKeys.keyOfAppJobClasses(appId);
        redis.opsForHash().delete(appJobClassesKey, clazz);
        return Boolean.TRUE;
    }

    @Override
    public Long countByAppId(Long appId) {
        return redis.opsForList().size(RedisKeys.keyOfAppJobs(appId));
    }

    @Override
    public List<Job> listByAppId(Long appId, Integer offset, Integer limit) {
        return list(RedisKeys.keyOfAppJobs(appId), offset, limit);
    }
}
