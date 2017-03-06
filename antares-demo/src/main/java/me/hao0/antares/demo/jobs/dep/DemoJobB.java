package me.hao0.antares.demo.jobs.dep;

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
public class DemoJobB implements DefaultJob {

    private final Random random = new Random();

    @Override
    public JobResult execute(JobContext context) {
        System.out.println("DemoJobB start...");
        System.out.println("context: " + context);

        Sleeps.sleep(random.nextInt(5) + 1);

        System.out.println("DemoJobB end...");

        return JobResult.SUCCESS;
    }
}
