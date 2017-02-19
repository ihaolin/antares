package me.hao0.antares.client.job.execute;

import me.hao0.antares.common.support.Lifecycle;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface JobExecutor extends Lifecycle {

    /**
     * Execute the job
     * @param instanceId the job instance id
     * @param zkJob the zk job
     */
    void execute(Long instanceId, ZkJob zkJob);
}
