package me.hao0.antares.common.model;

import me.hao0.antares.common.anno.RedisModel;
import me.hao0.antares.common.model.enums.JobInstanceStatus;
import me.hao0.antares.common.model.enums.JobTriggerType;

import java.util.Date;

/**
 * The job execution instance
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@RedisModel(prefix = "job_inss")
public class JobInstance implements Model<Long> {

    private static final long serialVersionUID = -6691569994755828004L;

    /**
     * The primary key
     */
    private Long id;

    /**
     * The job id
     */
    private Long jobId;

    /**
     * The job instance status
     * @see JobInstanceStatus
     */
    private Integer status;

    /**
     * The trigger type
     * @see JobTriggerType
     */
    private Integer triggerType;

    /**
     * The server, scheduling this job
     */
    private String server;

    /**
     * The max shard pull count
     * <p>
     *     the snapshot of the job config's max shard pull count, because it will be updated in the future
     * </p>
     */
    private Integer maxShardPullCount;

    /**
     * The job params
     * <p>
     *     the snapshot of the job config's params, because it will be updated in the future
     * </p>
     */
    private String jobParam;

    /**
     * The total shard count
     */
    private Integer totalShardCount;

    /**
     * The instance execution start time
     */
    private Date startTime;

    /**
     * The instance execution end time
     */
    private Date endTime;

    /**
     * The cause when failed
     */
    private String cause;

    /**
     * The created time
     */
    private Date ctime;

    /**
     * The updated time
     */
    private Date utime;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(Integer triggerType) {
        this.triggerType = triggerType;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Integer getMaxShardPullCount() {
        return maxShardPullCount;
    }

    public void setMaxShardPullCount(Integer maxShardPullCount) {
        this.maxShardPullCount = maxShardPullCount;
    }

    public String getJobParam() {
        return jobParam;
    }

    public void setJobParam(String jobParam) {
        this.jobParam = jobParam;
    }

    public Integer getTotalShardCount() {
        return totalShardCount;
    }

    public void setTotalShardCount(Integer totalShardCount) {
        this.totalShardCount = totalShardCount;
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

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public Date getCtime() {
        return ctime;
    }

    @Override
    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public Date getUtime() {
        return utime;
    }

    @Override
    public void setUtime(Date utime) {
        this.utime = utime;
    }

    @Override
    public String toString() {
        return "JobInstance{" +
                "id=" + id +
                ", jobId=" + jobId +
                ", status=" + status +
                ", triggerType=" + triggerType +
                ", server='" + server + '\'' +
                ", maxShardPullCount=" + maxShardPullCount +
                ", jobParam='" + jobParam + '\'' +
                ", totalShardCount=" + totalShardCount +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", cause='" + cause + '\'' +
                ", ctime=" + ctime +
                ", utime=" + utime +
                '}';
    }
}
