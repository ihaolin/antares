package me.hao0.antares.client.job;

/**
 * The simple job context
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class JobContextImpl implements JobContext {

    protected Long instanceId;

    protected String jobParam;

    protected Long shardId;

    protected Integer shardItem;

    protected String shardParam;

    protected Integer totalShardCount;

    @Override
    public Long getInstanceId() {
        return instanceId;
    }

    @Override
    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    @Override
    public String getJobParam() {
        return jobParam;
    }

    @Override
    public void setJobParam(String jobParam) {
        this.jobParam = jobParam;
    }

    @Override
    public Long getShardId() {
        return shardId;
    }

    public void setShardId(Long shardId) {
        this.shardId = shardId;
    }

    @Override
    public Integer getShardItem() {
        return shardItem;
    }

    @Override
    public void setShardItem(Integer shardItem) {
        this.shardItem = shardItem;
    }

    @Override
    public String getShardParam() {
        return shardParam;
    }

    @Override
    public void setShardParam(String shardParam) {
        this.shardParam = shardParam;
    }

    @Override
    public Integer getTotalShardCount() {
        return totalShardCount;
    }

    @Override
    public void setTotalShardCount(Integer totalShardCount) {
        this.totalShardCount = totalShardCount;
    }

    @Override
    public String toString() {
        return "JobContextImpl{" +
                "instanceId=" + instanceId +
                ", jobParam='" + jobParam + '\'' +
                ", shardId=" + shardId +
                ", shardItem=" + shardItem +
                ", shardParam='" + shardParam + '\'' +
                ", totalShardCount=" + totalShardCount +
                '}';
    }
}
