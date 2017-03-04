package me.hao0.antares.client.job;

import me.hao0.antares.common.util.Sleeps;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class DemoJobA implements DefaultJob {

    private final Random random = new Random();

    private final AtomicLong counter = new AtomicLong(0);

    @Override
    public JobResult execute(JobContext context) {
        System.out.println("DemoJobA start...");
        System.out.println("context: " + context);

        Sleeps.sleep(random.nextInt(10) + 1);

        System.out.println("DemoJobA end...");

        return JobResult.SUCCESS;
    }
}
