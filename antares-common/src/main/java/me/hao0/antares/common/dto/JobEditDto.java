package me.hao0.antares.common.dto;

import me.hao0.antares.common.model.enums.RecordType;

import java.io.Serializable;

/**
 * The job Edit dto
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class JobEditDto implements Serializable {

    private static final long serialVersionUID = -5130877794594938052L;

    private Long appId;

    private Long jobId;

    private Boolean status;

    private String clazz;

    private String cron;

    private String desc;

    private Integer maxShardPullCount;

    private String param;

    private Integer shardCount;

    private String shardParams;

    private Boolean misfire;

    private Long timeout;

    private RecordType recordType;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getMaxShardPullCount() {
        return maxShardPullCount;
    }

    public void setMaxShardPullCount(Integer maxShardPullCount) {
        this.maxShardPullCount = maxShardPullCount;
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

    public Boolean getMisfire() {
        return misfire;
    }

    public void setMisfire(Boolean misfire) {
        this.misfire = misfire;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public RecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }

    @Override
    public String toString() {
        return "JobEditDto{" +
                "appId=" + appId +
                ", jobId=" + jobId +
                ", status=" + status +
                ", clazz='" + clazz + '\'' +
                ", cron='" + cron + '\'' +
                ", desc='" + desc + '\'' +
                ", maxShardPullCount=" + maxShardPullCount +
                ", param='" + param + '\'' +
                ", shardCount=" + shardCount +
                ", shardParams='" + shardParams + '\'' +
                ", misfire=" + misfire +
                ", timeout=" + timeout +
                ", recordType=" + recordType +
                '}';
    }
}
