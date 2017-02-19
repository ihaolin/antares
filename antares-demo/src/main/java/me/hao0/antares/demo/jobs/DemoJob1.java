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
public class DemoJob1 implements DefaultJob {

    @Override
    public JobResult execute(JobContext context) {

        System.err.println("DemoJob1 start...");

        // sleep time > cron interval time
        Sleeps.sleep(25);

        System.err.println("DemoJob1 end");

        return JobResult.SUCCESS;
    }
}
