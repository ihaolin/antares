package me.hao0.antares.server.event.job;


/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class JobFinishedEvent extends JobEvent {

    private Long jobInstanceId;

    public JobFinishedEvent(Long jobId, Long jobInstanceId) {
        super(jobId);
        this.jobInstanceId = jobInstanceId;
    }

    public Long getJobInstanceId() {
        return jobInstanceId;
    }

    @Override
    public String toString() {
        return "JobFinishedEvent{" +
                "jobInstanceId=" + jobInstanceId +
                "} " + super.toString();
    }
}
