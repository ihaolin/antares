package me.hao0.antares.store.manager;

import me.hao0.antares.common.dto.ShardFinishDto;
import me.hao0.antares.common.log.Logs;
import me.hao0.antares.common.model.JobInstanceShard;
import me.hao0.antares.common.model.enums.JobInstanceShardStatus;
import me.hao0.antares.common.model.enums.ShardOperateRespCode;
import me.hao0.antares.store.dao.JobInstanceShardDao;
import me.hao0.antares.store.exception.ShardOperateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.Date;

/**
 * The job instance's shards manager
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Repository
public class JobInstanceShardManager {

    @Autowired
    private JobInstanceShardDao jobInstanceShardDao;

    /**
     * Save the job instance
     * @param progress the job instance progress
     * @return return true if save successfully, or false
     */
    public Boolean save(JobInstanceShard progress){

        if (jobInstanceShardDao.save(progress)){
            if (jobInstanceShardDao.bindInstance(progress.getInstanceId(), progress.getId())){
                return Boolean.TRUE;
            } else {
                // try to delete the dirty data
                jobInstanceShardDao.delete(progress.getId());
            }
        }

        return Boolean.FALSE;
    }

    /**
     * Delete the job instance progress
     * @param shardId the job instance shard id
     * @return return true if delete successfully, or false
     */
    public Boolean delete(Long shardId){
        JobInstanceShard progress = jobInstanceShardDao.findById(shardId);
        if (progress == null){
            return Boolean.TRUE;
        }

        if (jobInstanceShardDao.unbindInstance(progress.getInstanceId(), shardId)){
            return jobInstanceShardDao.delete(shardId);
        }

        return Boolean.FALSE;
    }

    /**
     * Get and update the shard for pulling shard
     * @param jobInstanceId the job instance id
     * @param maxShardPullCount the max shard pull count
     * @param client the client host
     * @return the shard
     */
    public JobInstanceShard pullShard(Long jobInstanceId, String client, Integer maxShardPullCount) {

        Long shardId = jobInstanceShardDao.pullShardFromNewShardsSet(jobInstanceId);
        if (shardId == null){
            // all shards are pulled
            throw new ShardOperateException(ShardOperateRespCode.SHARD_NO_AVAILABLE);
        }

        JobInstanceShard shard = checkShardStatus(shardId);

        // check the max pull count
        if (shard.getPullCount() > maxShardPullCount){
            // we think the shard is failed finally
            shard.setStatus(JobInstanceShardStatus.FAILED.value());
            jobInstanceShardDao.save(shard);
            jobInstanceShardDao.addShard2StatusSet(shard.getInstanceId(), shard.getId(), JobInstanceShardStatus.FAILED);
            throw new ShardOperateException(ShardOperateRespCode.SHARD_PULL_COUNT_EXCEED);
        }

        // add the shard to client's running set
        jobInstanceShardDao.addShard2ClientRunningSet(client, shardId);

        // add the shard to running shard
        jobInstanceShardDao.addShard2StatusSet(jobInstanceId, shardId, JobInstanceShardStatus.RUNNING);

        // update the shard
        shard.setPullCount(shard.getPullCount() + 1);
        shard.setPullTime(new Date());
        shard.setStatus(JobInstanceShardStatus.RUNNING.value());
        shard.setPullClient(client);
        if (!jobInstanceShardDao.save(shard)){
            throw new ShardOperateException(ShardOperateRespCode.SHARD_PULL_FAILED);
        }


        return shard;
    }

    /**
     * Return the shard
     * @param jobInstanceId the job instance id
     * @param shardId the shard id
     * @param client the client
     * @return return true if return successfully, or false
     */
    public Boolean returnShard(Long jobInstanceId, Long shardId, String client) {

        JobInstanceShard shard = checkShardStatus(shardId);

        // reset the shard
        shard.setStatus(JobInstanceShardStatus.NEW.value());
        shard.setPullClient(null);
        shard.setPullTime(null);
        shard.setUtime(new Date());

        // return the shard to shards set
        jobInstanceShardDao.returnShard2NewShardsSet(jobInstanceId, shardId);

        // remove the shard from running set
        jobInstanceShardDao.removeShardFromStatusSet(jobInstanceId, shardId, JobInstanceShardStatus.RUNNING);

        // remove the shard from client's running set
        jobInstanceShardDao.removeShardFromClientRunningShards(client, shardId);

        return jobInstanceShardDao.save(shard);
    }

    /**
     * Finish one shard
     * @param shardFinishDto the shard finish dto
     * @return return true if finish successfully, or false
     */
    public Boolean finishShard(ShardFinishDto shardFinishDto) {

        // check shard
        Long shardId = shardFinishDto.getShardId();
        JobInstanceShard shard = checkShardStatus(shardId);

        Long instanceId = shardFinishDto.getInstanceId();

        // add the shard to success or fail set
        if (shardFinishDto.getSuccess()){
            jobInstanceShardDao.addShard2StatusSet(instanceId, shardId, JobInstanceShardStatus.SUCCESS);
        } else {
            jobInstanceShardDao.addShard2StatusSet(instanceId, shardId, JobInstanceShardStatus.FAILED);
        }

        // remove the shard from running set
        jobInstanceShardDao.removeShardFromStatusSet(instanceId, shardId, JobInstanceShardStatus.RUNNING);

        // remove the shard from client's running set
        jobInstanceShardDao.removeShardFromClientRunningShards(shardFinishDto.getClient(), shardId);

        // update the shard
        if(shardFinishDto.getSuccess()){
            shard.setStatus(JobInstanceShardStatus.SUCCESS.value());
        } else {
            shard.setStatus(JobInstanceShardStatus.FAILED.value());
            shard.setCause(shardFinishDto.getCause());
        }
        shard.setStartTime(shardFinishDto.getStartTime());
        shard.setEndTime(shardFinishDto.getEndTime());
        shard.setFinishClient(shardFinishDto.getClient());
        shard.setUtime(new Date());

        return jobInstanceShardDao.save(shard);
    }

    private JobInstanceShard checkShardStatus(Long shardId){

        JobInstanceShard shard = jobInstanceShardDao.findById(shardId);
        if (shard == null){
            Logs.warn("The job shard(id={}) doesn't exist when finish.", shardId);
            throw new ShardOperateException(ShardOperateRespCode.SHARD_NOT_EXIST);
        }

        JobInstanceShardStatus shardStatus = JobInstanceShardStatus.from(shard.getStatus());
        if (shardStatus == JobInstanceShardStatus.SUCCESS
                || shardStatus == JobInstanceShardStatus.FAILED){
            Logs.warn("The job shard(id={})'s status is final: {}", shardId, shardStatus);
            throw new ShardOperateException(ShardOperateRespCode.SHARD_FINAL);
        }

        return shard;
    }
}
