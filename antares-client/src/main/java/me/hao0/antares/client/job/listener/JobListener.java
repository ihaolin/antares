package me.hao0.antares.client.job.listener;

import me.hao0.antares.client.job.JobContext;
import me.hao0.antares.client.job.JobResult;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface JobListener {

    /**
     * Callback when job execute before
     * @param context the job context
     */
    void onBefore(JobContext context);

    /**
     * Callback when job execute after
     * @param context
     */
    void onAfter(JobContext context, JobResult res);
}
