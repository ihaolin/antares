package me.hao0.antares.common.model;

import me.hao0.antares.common.model.enums.JobStatus;
import me.hao0.antares.common.model.enums.JobType;

import java.util.Date;

/**
 * The job basic info
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class Job implements Model<Long> {

    private static final long serialVersionUID = 6784880080835250983L;

    /**
     * The primary key
     */
    private Long id;

    /**
     * The application id
     */
    private Long appId;

    /**
     * The job type
     * @see JobType
     */
    private Integer type;

    /**
     * The job class full name
     */
    private String clazz;

    /**
     * The job cron expression
     */
    private String cron;

    /**
     * The job status
     * @see JobStatus
     */
    private Integer status;

    /**
     * The job description
     */
    private String desc;

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

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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
        return "Job{" +
                "id=" + id +
                ", appId=" + appId +
                ", type=" + type +
                ", clazz='" + clazz + '\'' +
                ", cron='" + cron + '\'' +
                ", status=" + status +
                ", desc='" + desc + '\'' +
                ", ctime=" + ctime +
                ", utime=" + utime +
                '}';
    }
}
