package me.hao0.antares.store.dao.impl;

import com.google.common.base.Strings;
import me.hao0.antares.common.model.JobConfig;
import me.hao0.antares.store.dao.JobConfigDao;
import me.hao0.antares.store.support.RedisKeys;
import org.springframework.stereotype.Repository;

/**
 * The job config dao
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Repository
public class JobConfigDaoImpl extends RedisDao<JobConfig> implements JobConfigDao {

    @Override
    public Boolean bindJob(Long jobId, Long jobConfigId) {
        String jobConfigMappingsKey = RedisKeys.JOB_CONFIG_MAPPINGS;
        redis.opsForHash().put(jobConfigMappingsKey, jobId.toString(), jobConfigId.toString());
        return Boolean.TRUE;
    }

    @Override
    public Boolean unbindJob(Long jobId, Long jobConfigId) {
        String jobConfigMappingsKey = RedisKeys.JOB_CONFIG_MAPPINGS;
        redis.opsForHash().delete(jobConfigMappingsKey, jobId.toString());
        return Boolean.TRUE;
    }

    @Override
    public JobConfig findByJobId(Long jobId) {
        String jobConfigMappingsKey = RedisKeys.JOB_CONFIG_MAPPINGS;
        String configId = String.valueOf(redis.opsForHash().get(jobConfigMappingsKey, jobId.toString()));
        if (Strings.isNullOrEmpty(configId)){
            return null;
        }
        return findById(Long.valueOf(configId));
    }
}
