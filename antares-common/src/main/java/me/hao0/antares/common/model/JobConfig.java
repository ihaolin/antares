package me.hao0.antares.common.model;

import me.hao0.antares.common.anno.RedisModel;
import java.util.Date;

/**
 * The job configuration info
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@RedisModel(prefix = "job_cfgs")
public class JobConfig implements Model<Long> {

    private static final long serialVersionUID = 4800351890221647029L;

    /**
     * The primary key
     */
    private Long id;

    /**
     * The job id
     */
    private Long jobId;

    /**
     * Support misfire or not
     */
    private Boolean misfire;

    /**
     * The job execute param(optional)
     */
    private String param;

    /**
     * The sharding total count
     */
    private Integer shardCount;

    /**
     * The sharding param, comma separated
     */
    private String shardParams;

    /**
     * The shard max pull count
     */
    private Integer maxShardPullCount;

    /**
     * The created time
     */
    private Date ctime;

    /**
     * The updated time
     */
    private Date utime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Boolean getMisfire() {
        return misfire;
    }

    public void setMisfire(Boolean misfire) {
        this.misfire = misfire;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Integer getShardCount() {
        return shardCount;
    }

    public void setShardCount(Integer shardCount) {
        this.shardCount = shardCount;
    }

    public String getShardParams() {
        return shardParams;
    }

    public void setShardParams(String shardParams) {
        this.shardParams = shardParams;
    }

    public Integer getMaxShardPullCount() {
        return maxShardPullCount;
    }

    public void setMaxShardPullCount(Integer maxShardPullCount) {
        this.maxShardPullCount = maxShardPullCount;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public Date getUtime() {
        return utime;
    }

    public void setUtime(Date utime) {
        this.utime = utime;
    }

    @Override
    public String toString() {
        return "JobConfig{" +
                "id=" + id +
                ", jobId=" + jobId +
                ", misfire=" + misfire +
                ", param='" + param + '\'' +
                ", shardCount=" + shardCount +
                ", shardParams='" + shardParams + '\'' +
                ", ctime=" + ctime +
                ", utime=" + utime +
                '}';
    }
}
