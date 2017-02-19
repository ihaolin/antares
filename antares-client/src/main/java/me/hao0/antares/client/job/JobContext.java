package me.hao0.antares.client.job;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface JobContext {

    /**
     * Get the job instance id
     * @return the job instance id
     */
    Long getInstanceId();

    void setInstanceId(Long instanceId);

    /**
     * Get the job param
     */
    String getJobParam();

    void setJobParam(String jobParam);

    /**
     * Get the job instance's shard id
     * @return the shard id
     */
    Long getShardId();

    void setShardId(Long shardId);

    /**
     * Get the shard item index
     * @return the shard item index
     */
    Integer getShardItem();

    void setShardItem(Integer shardItem);

    /**
     * Get the shard item's param
     * @return the shard's param
     */
    String getShardParam();

    void setShardParam(String shardParam);

    /**
     * Get the total shard count
     * @return the total shard count
     */
    Integer getTotalShardCount();

    void setTotalShardCount(Integer totalShardCount);
}
