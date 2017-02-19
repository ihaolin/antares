package me.hao0.antares.store.dao;

import me.hao0.antares.common.model.JobConfig;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface JobConfigDao extends BaseDao<JobConfig> {

    /**
     * Bind the job config to the job
     * @param jobId the job id
     * @param jobConfigId the job config id
     * @return return true if bind successfully, or false
     */
    Boolean bindJob(Long jobId, Long jobConfigId);

    /**
     * Unbind the job config from the job
     * @param jobId the job id
     * @param jobConfigId the job config id
     * @return return true if unbind successfully, or false
     */
    Boolean unbindJob(Long jobId, Long jobConfigId);

    /**
     * Find the job config
     * @param jobId the job id
     * @return the job config, or null if it doesn't exist
     */
    JobConfig findByJobId(Long jobId);
}
