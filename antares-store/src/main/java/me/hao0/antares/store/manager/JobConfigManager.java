package me.hao0.antares.store.manager;

import me.hao0.antares.common.model.JobConfig;
import me.hao0.antares.store.dao.JobConfigDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Repository
public class JobConfigManager {

    @Autowired
    private JobConfigDao jobConfigDao;

    /**
     * Save the job config
     * @param config the job config
     * @return return ture if save successfully, or false
     */
    public Boolean save(JobConfig config){

        if (jobConfigDao.save(config)){

            if (jobConfigDao.bindJob(config.getJobId(), config.getId())){
                return Boolean.TRUE;
            } else {
                // try delete the dirty config
                jobConfigDao.delete(config.getId());
            }
        }

        return Boolean.FALSE;
    }

    /**
     * Delete the job config
     * @param jobConfigId the job config id
     * @return return true if delete successfully, or false
     */
    public Boolean delete(Long jobConfigId){
        JobConfig cfg = jobConfigDao.findById(jobConfigId);
        if (cfg == null){
            return Boolean.TRUE;
        }

        if (jobConfigDao.unbindJob(cfg.getJobId(), cfg.getId())){
            return jobConfigDao.delete(jobConfigId);
        }

        return Boolean.FALSE;
    }

    /**
     * Delete the job config
     * @param cfg the job config id
     * @return return true if delete successfully, or false
     */
    public Boolean delete(JobConfig cfg){

        if (jobConfigDao.unbindJob(cfg.getJobId(), cfg.getId())){
            return jobConfigDao.delete(cfg.getId());
        }

        return Boolean.FALSE;
    }

    /**
     * Delete the job config
     * @param jobId the job id
     * @return return true if delete successfully, or false
     */
    public Boolean deleteByJobId(Long jobId) {

        JobConfig config = jobConfigDao.findByJobId(jobId);
        if (config != null){
            return delete(config);
        }

        return Boolean.FALSE;
    }
}
