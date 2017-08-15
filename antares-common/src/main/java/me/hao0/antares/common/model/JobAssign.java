package me.hao0.antares.common.model;

import java.util.Date;

/**
 * The job assignment
 * Author : haolin
 * Email  : haolin.h0@gmail.com
 */
public class JobAssign implements Model<Long> {

    /**
     * The job id
     */
    private Long jobId;

    /**
     * The assigned client ip
     */
    private String ip;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public Long getId() {
        return 0L;
    }

    @Override
    public void setId(Long id) {

    }

    @Override
    public void setCtime(Date ctime) {

    }

    @Override
    public void setUtime(Date utime) {

    }

    @Override
    public String toString() {
        return "JobAssign{" +
                "jobId=" + jobId +
                ", ip='" + ip + '\'' +
                '}';
    }
}
