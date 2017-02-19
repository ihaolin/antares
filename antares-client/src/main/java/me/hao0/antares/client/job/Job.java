package me.hao0.antares.client.job;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface Job {

    /**
     * Execute the job
     * @param context the job context
     * @return the job result
     */
    JobResult execute(JobContext context);

}
