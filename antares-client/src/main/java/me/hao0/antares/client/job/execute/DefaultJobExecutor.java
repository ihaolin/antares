package me.hao0.antares.client.job.execute;

import com.google.common.annotations.Beta;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import me.hao0.antares.client.core.AntaresClient;
import me.hao0.antares.client.job.Job;
import me.hao0.antares.common.dto.PullShard;
import me.hao0.antares.common.dto.ShardFinishDto;
import me.hao0.antares.common.dto.ShardOperateResp;
import me.hao0.antares.common.dto.ShardPullResp;
import me.hao0.antares.common.model.enums.ShardOperateRespCode;
import me.hao0.antares.common.retry.Retryer;
import me.hao0.antares.common.retry.Retryers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The default job executor
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Beta
public class DefaultJobExecutor extends AbstractJobExecutor implements JobExecutor {

    private static final Logger log = LoggerFactory.getLogger(DefaultJobExecutor.class);

    private final ConcurrentHashMap<Job, JobRetryer> jobRetryers = new ConcurrentHashMap<>();

    public DefaultJobExecutor(AntaresClient client) {
        super(client);
    }

    @Override
    protected PullShard pullShard(Long instanceId, ZkJob zkJob) {

        JobRetryer retryer = getJobRetryer(zkJob.getJob());

        ShardPullResp pullResp;
        try {
            pullResp = retryer.getPullRetryer().call(new RetryablePullShardTask(instanceId));
        } catch (Exception e) {
            log.error("failed to pull the shard, cause: {}", Throwables.getStackTraceAsString(e));
            return null;
        }

        if (pullResp == null) {
            return null;
        }

        checkInvalidInstance(instanceId, zkJob, pullResp.getCode());

        return pullResp.getPullShard();
    }

    @Override
    protected Boolean returnShard(Long instanceId, Long shardId, ZkJob zkJob) {
        JobRetryer retryer = getJobRetryer(zkJob.getJob());

        ShardOperateResp returnResp;
        try {
            returnResp = retryer.getReturnRetryer().call(new RetryableReturnShardTask(instanceId, shardId));
        } catch (Exception e) {
            log.error("failed to return the shard(jobInstanceId={}, shardId={}), cause: {}", instanceId, shardId, Throwables.getStackTraceAsString(e));
            return Boolean.FALSE;
        }

        if (returnResp == null) {
            return Boolean.TRUE;
        } else {
            checkInvalidInstance(instanceId, zkJob, returnResp.getCode());
        }

        return returnResp.getSuccess();
    }

    @Override
    protected Boolean finishShard(ShardFinishDto shardFinishDto, ZkJob zkJob) {

        JobRetryer retryer = getJobRetryer(zkJob.getJob());

        ShardOperateResp finishResp;
        try {
            finishResp = retryer.getFinishRetryer().call(new RetryableFinishShardTask(shardFinishDto));
        } catch (Exception e) {
            log.error("failed to finish the shard({}), cause: {}", shardFinishDto, Throwables.getStackTraceAsString(e));
            return Boolean.FALSE;
        }

        if (finishResp == null) {
            return Boolean.TRUE;
        } else {
            checkInvalidInstance(shardFinishDto.getInstanceId(), zkJob, finishResp.getCode());
        }

        return finishResp.getSuccess();
    }

    /**
     * Pull shard task
     */
    private class RetryablePullShardTask implements Callable<ShardPullResp> {

        private Long jobInstanceId;

        public RetryablePullShardTask(Long jobInstanceId) {
            this.jobInstanceId = jobInstanceId;
        }

        @Override
        public ShardPullResp call() throws Exception {
            return client.getHttp().pullJobInstanceShard(jobInstanceId);
        }
    }

    /**
     * Finish shard task
     */
    private class RetryableFinishShardTask implements Callable<ShardOperateResp> {

        private ShardFinishDto shardFinishDto;

        public RetryableFinishShardTask(ShardFinishDto shardFinishDto) {
            this.shardFinishDto = shardFinishDto;
        }

        @Override
        public ShardOperateResp call() throws Exception {
            return client.getHttp().finishJobInstanceShard(shardFinishDto);
        }
    }

    /**
     * Push shard task
     */
    private class RetryableReturnShardTask implements Callable<ShardOperateResp> {

        private Long instanceId;

        private Long shardId;

        public RetryableReturnShardTask(Long instanceId, Long shardId) {
            this.instanceId = instanceId;
            this.shardId = shardId;
        }

        @Override
        public ShardOperateResp call() throws Exception {
            return client.getHttp().returnJobInstanceShard(instanceId, shardId);
        }
    }

    private JobRetryer getJobRetryer(Job job) {

        JobRetryer retryer = jobRetryers.get(job);

        if (retryer == null){

            Retryer<ShardPullResp> pullShardRetryer = Retryers.get().newRetryer(new Predicate<ShardPullResp>() {
                @Override
                public boolean apply(ShardPullResp shardPullResp) {
                    return ShardOperateRespCode.needPullAgain(shardPullResp.getCode());
                }
            }, 5);

            Retryer<ShardOperateResp> finishShardRetryer = Retryers.get().newRetryer(new Predicate<ShardOperateResp>() {
                @Override
                public boolean apply(ShardOperateResp finishResp) {
                    return ShardOperateRespCode.needFinishAgain(finishResp.getCode());
                }
            }, 5);

            Retryer<ShardOperateResp> returnShardRetryer = Retryers.get().newRetryer(new Predicate<ShardOperateResp>() {
                @Override
                public boolean apply(ShardOperateResp returnResp) {
                    return ShardOperateRespCode.needReturnAgain(returnResp.getCode());
                }
            }, 5);

            retryer = new JobRetryer(job, pullShardRetryer, finishShardRetryer, returnShardRetryer);

            jobRetryers.putIfAbsent(job, retryer);
        }

        return retryer;
    }
}
