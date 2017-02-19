package me.hao0.antares.store.dao;

import me.hao0.antares.common.model.JobInstance;
import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface JobInstanceDao extends BaseDao<JobInstance> {

    /**
     * Bind the job instance to the job
     * @param jobId the job id
     * @param jobInstanceId the job instance id
     * @return return true if
     */
    Boolean bindJob(Long jobId, Long jobInstanceId);

    /**
     * Unbind the job instance from the job
     * @param jobId the job id
     * @param jobInstanceId the job instance id
     * @return return true if delete successfully, or false
     */
    Boolean unbindJob(Long jobId, Long jobInstanceId);

    /**
     * Count the instance of the job
     * @param jobId the job id
     * @return the instance count of the job
     */
    Long countByJobId(Long jobId);

    /**
     * List the instances of the job
     * @param jobId the job id
     * @param offset the offset
     * @param limit the limit
     * @return the instances of the job
     */
    List<JobInstance> listByJobId(Long jobId, Integer offset, Integer limit);

    /**
     * Find the max instance id of the job
     * @param jobId the job id
     * @return the max instance id of the job
     */
    Long findMaxId(Long jobId);
}
