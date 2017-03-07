package me.hao0.antares.demo.jobs;

import me.hao0.antares.client.job.DefaultJob;
import me.hao0.antares.client.job.JobContext;
import me.hao0.antares.client.job.JobResult;
import me.hao0.antares.common.util.Sleeps;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Component
public class DemoJobC implements DefaultJob {

    private final Random random = new Random();

    @Override
    public JobResult execute(JobContext context) {
        System.out.println("DemoJobC start...");
        System.out.println("context: " + context);

        Sleeps.sleep(random.nextInt(5) + 1);

        System.out.println("DemoJobC end...");

        return JobResult.SUCCESS;
    }
}
