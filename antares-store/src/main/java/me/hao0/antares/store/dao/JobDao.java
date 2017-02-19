package me.hao0.antares.store.dao;

import me.hao0.antares.common.model.Job;
import java.util.List;

/**
 * The job dao
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface JobDao extends BaseDao<Job> {

    /**
     * Bind the job to the app
     * @param appId the app id
     * @param jobId the job id
     * @return return true if bind successfully, or false
     */
    Boolean bindApp(Long appId, Long jobId);

    /**
     * Unbind the job from the app
     * @param appId the app id
     * @param jobId the job id
     * @return return true if unbind success, or false
     */
    Boolean unbindApp(Long appId, Long jobId);

    /**
     * Index the class of the job
     * @param appId the app id
     * @param jobId the job id
     * @param clazz the job class full name
     * @return return true if index successfully, or false
     */
    Boolean indexJobClass(Long appId, Long jobId, String clazz);

    /**
     * find the job of the class
     * @param appId the app id
     * @param clazz the job class full name
     * @return the job
     */
    Job findByJobClass(Long appId, String clazz);

    /**
     * find the job id of the class
     * @param appId the app id
     * @param clazz the job class full name
     * @return the job id
     */
    Long findIdByJobClass(Long appId, String clazz);

    /**
     * Unindex the class of the job
     * @param appId the app id
     * @param clazz the job class full name
     * @return return true if unindex successfully, or false
     */
    Boolean unIndexJobClass(Long appId, String clazz);

    /**
     * Count the job of the app
     * @param appId the app id
     * @return the job count of the app
     */
    Long countByAppId(Long appId);

    /**
     * List the jobs of the app
     * @param appId the app id
     * @param offset the offset
     * @param limit the limit
     * @return the jobs of the app
     */
    List<Job> listByAppId(Long appId, Integer offset, Integer limit);
}
