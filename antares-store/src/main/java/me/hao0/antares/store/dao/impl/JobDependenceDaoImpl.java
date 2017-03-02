package me.hao0.antares.store.dao.impl;

import me.hao0.antares.common.model.JobDependence;
import me.hao0.antares.store.dao.JobDependenceDao;
import me.hao0.antares.store.support.RedisKeys;
import me.hao0.antares.store.util.Page;
import org.springframework.stereotype.Repository;

/**
 * The job dependency dao
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Repository
public class JobDependenceDaoImpl extends RedisDao<JobDependence> implements JobDependenceDao {

    @Override
    public Boolean addDependence(JobDependence dependence) {
        String jobNextJobsKey = RedisKeys.keyOfJobNextJobs(dependence.getJobId());
        return redis.opsForList().leftPush(jobNextJobsKey, String.valueOf(dependence.getNextJobId())) > 0L;
    }

    @Override
    public Page<Long> pagingNextJobIds(Long jobId, Integer offset, Integer limit) {

        String jobNextJobsKey = RedisKeys.keyOfJobNextJobs(jobId);

        Long total = count(jobNextJobsKey);
        if (total <= 0L){
            return Page.empty();
        }

        return new Page<>(total, listIds(jobNextJobsKey, offset, limit));
    }

    @Override
    public Boolean deleteNextJobIds(Long jobId) {
        String jobNextJobsKey = RedisKeys.keyOfJobNextJobs(jobId);
        redis.delete(jobNextJobsKey);
        return Boolean.TRUE;
    }

    @Override
    public Boolean deleteNextJobId(Long jobId, Long nextJobId) {
        String jobNextJobsKey = RedisKeys.keyOfJobNextJobs(jobId);
        return redis.opsForList().remove(jobNextJobsKey, 1L, String.valueOf(nextJobId)) > 0L;
    }
}
