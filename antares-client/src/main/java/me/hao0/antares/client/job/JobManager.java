package me.hao0.antares.client.job;

import com.google.common.collect.Maps;
import me.hao0.antares.client.core.AntaresClient;
import me.hao0.antares.client.job.execute.ZkJob;
import me.hao0.antares.common.support.Lifecycle;
import me.hao0.antares.common.support.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class JobManager extends Component implements Lifecycle {

    private static final Logger log = LoggerFactory.getLogger(JobManager.class);

    private final AntaresClient client;

    private Map<String, ZkJob> jobs = Maps.newConcurrentMap();

    public JobManager(AntaresClient client) {
        this.client = client;
    }

    @Override
    public void doStart() {
        if (!jobs.isEmpty()){
            for (ZkJob job : jobs.values()){
                job.start();
            }
        }
    }

    @Override
    public void doShutdown() {
        if (!jobs.isEmpty()){
            for (ZkJob job : jobs.values()){
                job.shutdown();
            }
        }
    }

    /**
     * Register the job
     * @param job the job
     */
    public void registerJob(Job job) {

        final String jobClass = job.getClass().getName();

        if (jobs.containsKey(jobClass)) return;

        ZkJob zkJob = new ZkJob(client, job);
        zkJob.start();

        log.info("registered the job: {}", job);

        jobs.put(jobClass, zkJob);
    }

    public Map<String, ZkJob> getJobs() {
        return jobs;
    }
}
