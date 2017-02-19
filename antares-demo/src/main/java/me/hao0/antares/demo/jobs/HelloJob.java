package me.hao0.antares.demo.jobs;

import me.hao0.antares.client.job.DefaultJob;
import me.hao0.antares.client.job.JobContext;
import me.hao0.antares.client.job.JobResult;
import me.hao0.antares.common.util.Sleeps;
import org.springframework.stereotype.Component;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Component
public class HelloJob implements DefaultJob {

    @Override
    public JobResult execute(JobContext context) {

        System.err.println("HelloJob start...");

        // execute time > cron interval time
        Sleeps.sleep(40);

        System.err.println("HelloJob end");

        return JobResult.SUCCESS;
    }
}
