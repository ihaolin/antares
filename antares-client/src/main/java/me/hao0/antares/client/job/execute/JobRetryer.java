package me.hao0.antares.client.job.execute;

import me.hao0.antares.client.job.Job;
import me.hao0.antares.common.dto.ShardOperateResp;
import me.hao0.antares.common.dto.ShardPullResp;
import me.hao0.antares.common.retry.Retryer;

/**
 * The job retryer
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class JobRetryer {

    /**
     * The job
     */
    private final Job job;

    private final Retryer<ShardPullResp> pullRetryer;

    private final Retryer<ShardOperateResp> finishRetryer;

    private final Retryer<ShardOperateResp> returnRetryer;

    public JobRetryer(Job job, Retryer<ShardPullResp> pullRetryer, Retryer<ShardOperateResp> finishRetryer, Retryer<ShardOperateResp> returnRetryer) {
        this.job = job;
        this.pullRetryer = pullRetryer;
        this.finishRetryer = finishRetryer;
        this.returnRetryer = returnRetryer;
    }

    public Job getJob() {
        return job;
    }

    public Retryer<ShardPullResp> getPullRetryer() {
        return pullRetryer;
    }

    public Retryer<ShardOperateResp> getFinishRetryer() {
        return finishRetryer;
    }

    public Retryer<ShardOperateResp> getReturnRetryer() {
        return returnRetryer;
    }
}
