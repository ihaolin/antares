package me.hao0.antares.store.manager;

import me.hao0.antares.common.model.Job;
import me.hao0.antares.store.dao.JobDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Repository
public class JobManager {

    @Autowired
    private JobDao jobDao;

    /**
     * Save the job
     * @param job the job
     * @return return true if save successfully, or false
     */
    public Boolean save(Job job){

        boolean isCreate = job.getId() == null;

        boolean success = jobDao.save(job);
        if (success){

            if (isCreate){

                // bind job to app and index job class only creating
                if(jobDao.bindApp(job.getAppId(), job.getId())){
                    // index job class
                    success = jobDao.indexJobClass(job.getAppId(), job.getId(), job.getClazz());
                } else {
                    success = false;
                }

                if (!success){
                    // try to rollback if create failed
                    delete(job.getId());
                }
            }
        }

        return success;
    }

    /**
     * Delete the job
     * @param jobId the job id
     * @return return true if delete successfully
     */
    public Boolean delete(Long jobId){
        Job job = jobDao.findById(jobId);
        if (job == null){
            return Boolean.TRUE;
        }

        if (jobDao.unbindApp(job.getAppId(), jobId)){
            return jobDao.delete(jobId)
                        && jobDao.unIndexJobClass(job.getAppId(), job.getClazz());
        }

        return Boolean.FALSE;
    }
}
