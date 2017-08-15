package me.hao0.antares.store.dao;

import me.hao0.antares.common.model.JobAssign;
import java.util.Set;

/**
 * The job assign dao
 * Author : haolin
 * Email  : haolin.h0@gmail.com
 */
public interface JobAssignDao extends BaseDao<JobAssign> {

    /**
     * Add the job assignment
     * @return return true if add successfully, or false
     */
    Boolean addAssign(Long jobId, String... clientIps);

    /**
     * Remove the job assignment
     * @return return true if remove successfully, or false
     */
    Boolean removeAssign(Long jobId, Object... clientIps);

    /**
     * The ip is assigned or not
     * @param jobId the job id
     * @param ip the client ip
     * @return return true if the ip is assigned, or false
     */
    Boolean isAssigned(Long jobId, String ip);

    /**
     * List all job assigns
     * @param jobId the job id
     * @return the job assigns
     */
    Set<String> listAssigns(Long jobId);

    /**
     * Clean the job assign
     * @param jobId the job id
     * @return return true if clean successfully, or false
     */
    Boolean cleanAssign(Long jobId);
}
