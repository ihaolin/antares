package me.hao0.antares.store.dao;

import me.hao0.antares.common.model.JobServer;
import java.util.List;

/**
 * The job server relation dao
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface JobServerDao {

    /**
     * Bind the job to the server
     * @param jobServer the job server relation
     * @return return true if bind successfully, or false
     */
    Boolean bind(JobServer jobServer);

    /**
     * Unbind the server's all jobs
     * @param server the server
     * @return return true if unbind successfully, or false
     */
    Boolean unbindJobsOfServer(String server);

    /**
     * Unbind the job from server
     * @param jobId the job id
     * @return  return true if unbind successfully, or false
     */
    Boolean unbindJob(Long jobId);

    /**
     * Find the schedule server of the job
     * @param jobId the job id
     * @return the server of the job
     */
    String findServerByJobId(Long jobId);

    /**
     * Find the job id list of the server
     * @param server the server
     * @return the job id list of the server
     */
    List<Long> findJobsByServer(String server);

    /**
     * Count the job of the server
     * @param server the server
     * @return the job count of the server
     */
    Long countJobsByServer(String server);
}
