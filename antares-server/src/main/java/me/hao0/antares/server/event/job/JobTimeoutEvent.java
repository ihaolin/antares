package me.hao0.antares.server.event.job;


/**
 * The event when the job is timeout
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class JobTimeoutEvent extends JobEvent {

    private Long jobInstanceId;

    public JobTimeoutEvent(Long jobId, Long jobInstanceId) {
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
