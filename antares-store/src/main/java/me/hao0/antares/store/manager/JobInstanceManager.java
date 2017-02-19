package me.hao0.antares.store.manager;

import me.hao0.antares.common.model.JobInstance;
import me.hao0.antares.common.util.CollectionUtil;
import me.hao0.antares.common.util.Constants;
import me.hao0.antares.store.dao.JobInstanceDao;
import me.hao0.antares.store.dao.JobInstanceShardDao;
import me.hao0.antares.store.support.RedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Repository
public class JobInstanceManager {

    @Autowired
    private JobInstanceDao jobInstanceDao;

    @Autowired
    private JobInstanceShardDao jobInstanceShardDao;

    /**
     * Save the job instance
     * @param instance the job instance
     * @return return true if save successfully, or false
     */
    public Boolean create(JobInstance instance){

        if (jobInstanceDao.save(instance)){
            if (jobInstanceDao.bindJob(instance.getJobId(), instance.getId())){
                return Boolean.TRUE;
            } else {
                // try to rollback the dirty data
                jobInstanceDao.delete(instance.getId());
            }
        }

        return Boolean.FALSE;
    }

    /**
     * Delete the job instance
     * @param jobInstanceId the job instance id
     * @return return true if delete successfully, or false
     */
    public Boolean deleteById(Long jobInstanceId){

        JobInstance instance = jobInstanceDao.findById(jobInstanceId);
        if (instance == null){
            return Boolean.TRUE;
        }

        jobInstanceDao.unbindJob(instance.getJobId(), jobInstanceId);

        jobInstanceDao.delete(jobInstanceId);

        return Boolean.FALSE;
    }

    public Boolean deleteByJobId(Long jobId) {

        String jobInstancesKey = RedisKeys.keyOfJobInstances(jobId);

        Integer offset = 0;
        List<Long> instanceIds;
        for(;;){

            instanceIds = jobInstanceDao.listIds(jobInstancesKey, offset, Constants.DEFAULT_LIST_BATCH_SIZE);
            if (CollectionUtil.isNullOrEmpty(instanceIds)){
                break;
            }

            for (Long instanceId : instanceIds){

                // delete the instance's shards
                jobInstanceShardDao.deleteByInstanceId(instanceId);

                // delete the instance
                deleteById(instanceId);
            }

            offset += Constants.DEFAULT_LIST_BATCH_SIZE;
        }

        return Boolean.TRUE;
    }
}
