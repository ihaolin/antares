package me.hao0.antares.store.dao;

import me.hao0.antares.common.model.JobInstanceShard;
import me.hao0.antares.common.model.enums.JobInstanceShardStatus;
import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface JobInstanceShardDao extends BaseDao<JobInstanceShard> {

    /**
     * Bind the job instance progress to the job instance
     * @param instanceId the job instance id
     * @param progressId the job instance progress id
     * @return return true if bind successfully, or false
     */
    Boolean bindInstance(Long instanceId, Long progressId);

    /**
     * Unbind the job instance progress from the job instance
     * @param instanceId the job instance id
     * @param progressId the job instance progress id
     * @return return true if unbind successfully, or false
     */
    Boolean unbindInstance(Long instanceId, Long progressId);

    /**
     * Count the shards of the job instance
     * @param instanceId the job instance id
     * @return the instance count of the job
     */
    Long countByInstanceId(Long instanceId);

    /**
     * List the shards of the job instance
     * @param instanceId the job instance id
     * @param offset the offset
     * @param limit the limit
     * @return the progresses of the job instance
     */
    List<JobInstanceShard> listByInstanceId(Long instanceId, Integer offset, Integer limit);

    /**
     * Delete all progresses of the job instance
     * @param instanceId the job instance id
     * @return return true if delete successfully, or false
     */
    Boolean deleteByInstanceId(Long instanceId);

    /**
     * Create the job instance's shards set for pulling
     * @param instanceId the job instance id
     * @param shardIds the shard id list
     * @return return true if create successfully, or false
     */
    Boolean createNewShardsSet(Long instanceId, List<Long> shardIds);

    /**
     * Return a shard back to  the shards set
     * @param instanceId the job instance id
     * @param shardId the shard id
     * @return return true if push successfully, or false
     */
    Boolean returnShard2NewShardsSet(Long instanceId, Long shardId);

    /**
     * Pull a shard from the shards counter
     * @param instanceId the job instance id
     * @return the job instance's shard id, or null if all shard ids are pulled
     */
    Long pullShardFromNewShardsSet(Long instanceId);

    /**
     * Add one shard to the certain status set
     * @param instanceId the job instance id
     * @param shardId the shard id
     * @param status the shard statue
     * @see JobInstanceShardStatus
     * @return return true if add successfully, or false
     */
    Boolean addShard2StatusSet(Long instanceId, Long shardId, JobInstanceShardStatus status);

    /**
     * Remove one shard to the certain status set
     * @param instanceId the job instance id
     * @param shardId the shard id
     * @param status the shard statue
     * @see JobInstanceShardStatus
     * @return return true if remove successfully, or false
     */
    Boolean removeShardFromStatusSet(Long instanceId, Long shardId, JobInstanceShardStatus status);

    /**
     * Add the shard to the client's running set
     * @param client the client
     * @param shardId the shard id
     * @return return true if add successfully, or false
     */
    Boolean addShard2ClientRunningSet(String client, Long shardId);

    /**
     * Get the client's running shard id list
     * @param client the client
     * @return the client's running shard id list
     */
    List<Long> getClientRunningShards(String client);

    /**
     * Remove the shard id from the client's running shard
     * @param client the client
     * @param shardId the shard id
     * @return return true if remove successfully, or false
     */
    Boolean removeShardFromClientRunningShards(String client, Long shardId);

    /**
     * Get the total shard count
     * @param instanceId the job instance id
     * @return the total shard count
     */
    Integer getJobInstanceTotalShardCount(Long instanceId);

    /**
     * Get the job instance status shards' count
     * @param instanceId the job instance id
     * @param status the shard status
     * @return the job instance status shards' count
     */
    Integer getJobInstanceStatusShardCount(Long instanceId, JobInstanceShardStatus status);


}
