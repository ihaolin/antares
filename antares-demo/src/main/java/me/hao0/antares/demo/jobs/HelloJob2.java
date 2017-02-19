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
public class HelloJob2 implements DefaultJob {

    private final Random random = new Random();

    @Override
    public JobResult execute(JobContext context) {

        System.out.println("HelloJob2 start...");

        System.out.println("context: " + context);

        Sleeps.sleep(random.nextInt(8) + 1);

        System.out.println("HelloJob2 end...");

        return JobResult.SUCCESS;
    }
}
