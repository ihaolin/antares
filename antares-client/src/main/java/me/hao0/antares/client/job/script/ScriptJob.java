package me.hao0.antares.client.job.script;

import me.hao0.antares.client.job.Job;
import me.hao0.antares.client.job.JobContext;
import me.hao0.antares.client.job.JobResult;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public abstract class ScriptJob implements Job {

    @Override
    public JobResult execute(JobContext context) {
        return null;
    }
}
