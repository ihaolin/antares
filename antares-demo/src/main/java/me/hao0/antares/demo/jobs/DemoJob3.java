package me.hao0.antares.demo.jobs;

import me.hao0.antares.client.job.DefaultJob;
import me.hao0.antares.client.job.JobContext;
import me.hao0.antares.client.job.JobResult;
import me.hao0.antares.common.util.Sleeps;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Component
public class DemoJob3 implements DefaultJob {

    private final Random random = new Random();

    private final AtomicLong counter = new AtomicLong(0);

    @Override
    public JobResult execute(JobContext context) {
        System.out.println("DemoJob3 start...");
        System.out.println("context: " + context);

        Sleeps.sleep(random.nextInt(5) + 1);

        System.out.println("DemoJob3 end...");

        if (counter.getAndIncrement() % 15 == 0){
            // later will return back the shard
            return JobResult.LATER;
        }

        return JobResult.SUCCESS;
    }
}
