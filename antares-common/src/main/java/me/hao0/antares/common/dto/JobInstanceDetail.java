package me.hao0.antares.common.dto;

import java.io.Serializable;

/**
 * The job running detail for monitor
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 * <p>
 *     NOTE: This instance will be the one id is minimal, if there're multiple instances at the same time.
 * </p>
 */
public class JobInstanceDetail implements Serializable {

    private static final long serialVersionUID = -3208492213218789547L;

    /**
     * The job id
     */
    private Long jobId;

    /**
     * The job instance id
     */
    private Long instanceId;

    /**
     * The job instance status
     */
    private Integer status;

    /**
     * The job instance status desc
     */
    private String statusDesc;

    /**
     * The cause when failed
     */
    private String cause;

    /**
     * The instance execution start time
     */
    private String startTime;

    /**
     * The instance execution end time
     */
    private String endTime;

    /**
     * The total shard count
     * <p>
     *     totalShardCount = successShardCount + failedShardCount + waitShardCount
     * </p>
     */
    private Integer totalShardCount;

    /**
     * The wait pull shard count
     */
    private Integer waitShardCount;

    /**
     * The running shard count
     */
    private Integer runningShardCount;

    /**
     * The success shard count
     */
    private Integer successShardCount;

    /**
     * The failed shard count
     */
    private Integer failedShardCount;

    /**
     * The finish percent: finishShardCount * 100 / totalShardCount
     */
    private Integer finishPercent;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getTotalShardCount() {
        return totalShardCount;
    }

    public void setTotalShardCount(Integer totalShardCount) {
        this.totalShardCount = totalShardCount;
    }

    public Integer getSuccessShardCount() {
        return successShardCount;
    }

    public void setSuccessShardCount(Integer successShardCount) {
        this.successShardCount = successShardCount;
    }

    public Integer getFailedShardCount() {
        return failedShardCount;
    }

    public void setFailedShardCount(Integer failedShardCount) {
        this.failedShardCount = failedShardCount;
    }

    public Integer getWaitShardCount() {
        return waitShardCount;
    }

    public void setWaitShardCount(Integer waitShardCount) {
        this.waitShardCount = waitShardCount;
    }

    public Integer getRunningShardCount() {
        return runningShardCount;
    }

    public void setRunningShardCount(Integer runningShardCount) {
        this.runningShardCount = runningShardCount;
    }

    public Integer getFinishPercent() {
        return finishPercent;
    }

    public void setFinishPercent(Integer finishPercent) {
        this.finishPercent = finishPercent;
    }

    @Override
    public String toString() {
        return "JobInstanceDetail{" +
                "jobId=" + jobId +
                ", instanceId=" + instanceId +
                ", status=" + status +
                ", statusDesc='" + statusDesc + '\'' +
                ", cause='" + cause + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", totalShardCount=" + totalShardCount +
                ", waitShardCount=" + waitShardCount +
                ", runningShardCount=" + runningShardCount +
                ", successShardCount=" + successShardCount +
                ", failedShardCount=" + failedShardCount +
                ", finishPercent=" + finishPercent +
                '}';
    }
}
