package me.hao0.antares.server.event.job;

import me.hao0.antares.server.event.core.Event;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public abstract class JobEvent implements Event {

    protected Long jobId;

    public JobEvent(Long jobId) {
        this.jobId = jobId;
    }

    public Long getJobId() {
        return jobId;
    }

    @Override
    public String toString() {
        return "JobEvent{" +
                "jobId=" + jobId +
                '}';
    }
}
