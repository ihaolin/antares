package me.hao0.antares.client.job;

import me.hao0.antares.common.util.Sleeps;
import java.util.Random;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class DemoJobB implements DefaultJob {

    private final Random random = new Random();

    @Override
    public JobResult execute(JobContext context) {
        System.out.println("DemoJobB start...");
        System.out.println("context: " + context);

        Sleeps.sleep(random.nextInt(10) + 1);

        System.out.println("DemoJobB end...");

        return JobResult.SUCCESS;
    }
}
