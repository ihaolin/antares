package me.hao0.antares.server.event.job;


/**
 * The event when the job is timeout
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class JobTimeoutEvent extends JobEvent {

    private Long jobInstanceId;

    private String detail;

    public JobTimeoutEvent(Long jobId, Long jobInstanceId, String detail) {
        super(jobId);
        this.jobInstanceId = jobInstanceId;
        this.detail = detail;
    }

    public Long getJobInstanceId() {
        return jobInstanceId;
    }

    public String getDetail() {
        return detail;
    }

    @Override
    public String toString() {
        return "JobFinishedEvent{" +
                "jobInstanceId=" + jobInstanceId +
                "} " + super.toString();
    }
}
