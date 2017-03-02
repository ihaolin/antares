package me.hao0.antares.common.model;

import java.util.Date;

/**
 * The job dependency relation
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class JobDependence implements Model<Long> {

    private static final long serialVersionUID = 4771124996803116397L;

    /**
     * The job id
     */
    private Long jobId;

    /**
     * The next job id
     */
    private Long nextJobId;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getNextJobId() {
        return nextJobId;
    }

    public void setNextJobId(Long nextJobId) {
        this.nextJobId = nextJobId;
    }

    @Override
    public String toString() {
        return "JobDependence{" +
                "jobId=" + jobId +
                ", nextJobId=" + nextJobId +
                '}';
    }

    @Override
    public Long getId() {return 0L;}

    @Override
    public void setId(Long id) {}

    @Override
    public void setCtime(Date ctime) {}

    @Override
    public void setUtime(Date utime) {}
}
