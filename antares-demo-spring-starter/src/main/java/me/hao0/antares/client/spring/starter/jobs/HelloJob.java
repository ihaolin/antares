package me.hao0.antares.client.spring.starter.jobs;

import me.hao0.antares.client.job.DefaultJob;
import me.hao0.antares.client.job.JobContext;
import me.hao0.antares.client.job.JobResult;
import org.springframework.stereotype.Component;

/**
 * Author : haolin
 * Email  : haolin.h0@gmail.com
 */
@Component
public class HelloJob implements DefaultJob {

    @Override
    public JobResult execute(JobContext context) {

        System.err.println("Hello Job, " +  context.getShardItem());

        return JobResult.SUCCESS;
    }
}
