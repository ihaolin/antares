package me.hao0.antares.store.dao;

import me.hao0.antares.common.model.JobDependence;
import me.hao0.antares.store.util.Page;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface JobDependenceDao extends BaseDao<JobDependence> {

    /**
     * Save the job dependence
     * @param dependence the job dependence
     * @return return true if save successfully, or false
     */
    Boolean addDependence(JobDependence dependence);

    /**
     * Paging the job's next job ids
     * @param jobId the job id
     * @param offset the offset
     * @param limit the limit
     * @return the job's next job page ids
     */
    Page<Long> pagingNextJobIds(Long jobId, Integer offset, Integer limit);

    /**
     * Delete the job's next jobs
     * @param jobId the job id
     * @return return true if delete successfully, or false
     */
    Boolean deleteNextJobIds(Long jobId);

    /**
     * Delete the job's next job
     * @param jobId the job id
     * @param nextJobId the next job id
     * @return return true if delete successfully, or false
     */
    Boolean deleteNextJobId(Long jobId, Long nextJobId);
}
