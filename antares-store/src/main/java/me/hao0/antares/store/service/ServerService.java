package me.hao0.antares.store.service;

import me.hao0.antares.common.util.Response;
import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface ServerService {

    /**
     * Dispatch the job
     * @param jobId the job id
     * @return return true if dispatch successfully, or false
     */
    Response<Boolean> scheduleJob(Long jobId);

    /**
     * Schedule the job if possible
     * @param jobId the job id
     * @return return true if operate successfully, or false
     */
    Response<Boolean> scheduleJobIfPossible(Long jobId);

    /**
     * Dispatch the job
     * @param jobId the job id
     * @param servers the alive server list
     * @return return true if dispatch successfully, or false
     */
    Response<Boolean> scheduleJob(Long jobId, List<String> servers);

    /**
     * Dispatch the jobs
     * @param jobIds the job id list
     * @param servers the alive server list
     * @return return true if dispatch successfully, or false
     */
    Response<Boolean> scheduleJobs(List<Long> jobIds, final List<String> servers);

    /**
     * Trigger the job
     * @param jobId the job id
     * @return return true if trigger successfully, or false
     */
    Response<Boolean> triggerJob(Long jobId);

    /**
     * Notify the job to trigger
     * @param jobId the job id
     * @return return true if trigger successfully, or false
     */
    Response<Boolean> notifyJob(Long jobId);

    /**
     * Pause the job
     * @param jobId the job id
     * @return return true if pause successfully, or false
     */
    Response<Boolean> pauseJob(Long jobId);

    /**
     * Resume the job to schedule
     * @param jobId the job id
     * @return return true if resume successfully, or false
     */
    Response<Boolean> resumeJob(Long jobId);

    /**
     * Remove the job scheduling
     * @param jobId the job id
     * @return return true if remove successfully, or false
     */
    Response<Boolean> removeJob(Long jobId);

    /**
     * Reload the job scheduling
     * @param jobId the job id
     * @return return true if reload successfully, or false
     */
    Response<Boolean> reloadJob(Long jobId);
}
