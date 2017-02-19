package me.hao0.antares.common.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class ShardFinishDto implements Serializable {

    private static final long serialVersionUID = 7852332761131470264L;

    /**
     * The client host
     */
    private String client;

    /**
     * The job instance id
     */
    private Long instanceId;

    /**
     * The shard id
     */
    private Long shardId;

    /**
     * The shard start time
     */
    private Date startTime;

    /**
     * The shard end time
     */
    private Date endTime;

    /**
     * Finish success or false
     */
    private Boolean success = Boolean.TRUE;

    /**
     * The cause when failed
     */
    private String cause;

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Long getShardId() {
        return shardId;
    }

    public void setShardId(Long shardId) {
        this.shardId = shardId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return "ShardFinishDto{" +
                "client='" + client + '\'' +
                ", instanceId=" + instanceId +
                ", shardId=" + shardId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", success=" + success +
                ", cause='" + cause + '\'' +
                '}';
    }
}
