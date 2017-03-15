package me.hao0.antares.client.job;

import me.hao0.antares.common.util.Sleeps;

import java.util.Random;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class TimeoutJob implements DefaultJob {

    private final Random random = new Random();

    @Override
    public JobResult execute(JobContext context) {

        System.out.println("Timeout start...");

        System.out.println("context: " + context);

        // working longer than the cron expression
        Sleeps.sleep(35);

        System.out.println("Timeout end...");

        return JobResult.SUCCESS;
    }
}
