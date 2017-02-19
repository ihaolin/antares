package me.hao0.antares.client.job.execute;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import me.hao0.antares.client.core.AntaresClient;
import me.hao0.antares.client.job.DefaultJob;
import me.hao0.antares.client.job.Job;
import me.hao0.antares.client.job.JobContext;
import me.hao0.antares.client.job.JobResult;
import me.hao0.antares.client.job.JobContextImpl;
import me.hao0.antares.client.job.listener.JobListener;
import me.hao0.antares.client.job.listener.JobResultListener;
import me.hao0.antares.client.job.script.DefaultScriptExecutor;
import me.hao0.antares.client.job.script.ScriptJob;
import me.hao0.antares.client.job.script.ScriptExecutor;
import me.hao0.antares.common.dto.PullShard;
import me.hao0.antares.common.dto.ShardFinishDto;
import me.hao0.antares.common.model.enums.ShardOperateRespCode;
import me.hao0.antares.common.support.Component;
import static me.hao0.antares.common.util.Constants.*;
import me.hao0.antares.common.util.Executors;
import me.hao0.antares.common.util.Systems;
import me.hao0.antares.common.util.ZkPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * The abstract job executor
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public abstract class AbstractJobExecutor extends Component implements JobExecutor {

    private final Logger log = LoggerFactory.getLogger(AbstractJobExecutor.class);

    protected final AntaresClient client;

    private ExecutorService executor;

    /**
     * The script executor
     */
    private final ScriptExecutor scriptExecutor = new DefaultScriptExecutor();

    public AbstractJobExecutor(AntaresClient client) {
        this.client = client;
    }

    @Override
    public void doStart() {
       executor = Executors.newExecutor(client.getExecutorThreadCount(), 10000, "JOB-EXECUTOR-");
    }

    @Override
    public void doShutdown() {
        executor.shutdown();
    }

    public void execute(final Long instanceId, final ZkJob zkJob) {
        executor.submit(new ExecuteShardTask(instanceId, zkJob));
    }

    /**
     * The task for executing the job shard
     */
    private class ExecuteShardTask implements Runnable{

        private final Long instanceId;

        private final ZkJob zkJob;

        public ExecuteShardTask(Long instanceId, ZkJob zkJob) {
            this.instanceId = instanceId;
            this.zkJob = zkJob;
        }

        @Override
        public void run() {
            try {

                PullShard oneShard;
                for(;;){

                    // pull until has one shard, or the job instance is finished
                    oneShard = pullShard(instanceId, zkJob);
                    if (oneShard == null){
                        // no need to continue
                        break;
                    }

                    doExecuteShard(instanceId, zkJob, oneShard);

                    // continue to pull
                }

            } catch (Exception e){
                log.error("failed to execute shard task(job={}, instanceId={}), cause: {}",
                        zkJob.getJob(), instanceId, Throwables.getStackTraceAsString(e));
            }
        }
    }

    private void doExecuteShard(Long instanceId, ZkJob zkJob, PullShard oneShard) {

        // build the job context
        JobContext context = buildJobContext(instanceId, oneShard);

        // invoke the job
        Job job = zkJob.getJob();

        // listener on before execute
        if (job instanceof JobListener){
            ((JobListener) job).onBefore(context);
        }

        Date startTime = new Date();

        JobResult res = null;
        if (job instanceof DefaultJob){
            // execute common job
            res = job.execute(context);
        } else if (job instanceof ScriptJob){
            // execute script job
            res = executeScript(context);
        }

        Date endTime = new Date();

        // listener on after execute
        if (job instanceof JobListener){
            ((JobListener) job).onAfter(context, res);
        }

        // handle the job result
        if (res == null || res.is(JobResult.SUCCESS)){

            // success
            // write the instance shard to finish
            ShardFinishDto shardFinishDto = buildShardFinishDto(instanceId, context.getShardId(), startTime, endTime);
            shardFinishDto.setSuccess(Boolean.TRUE);
            finishShard(shardFinishDto, zkJob);

            // callback on success
            if (job instanceof JobResultListener){
                ((JobResultListener)job).onSuccess();
            }

        } else if(res.is(JobResult.FAIL)){

            // fail
            ShardFinishDto shardFinishDto = buildShardFinishDto(instanceId, context.getShardId(), startTime, endTime);
            shardFinishDto.setSuccess(Boolean.FALSE);
            shardFinishDto.setCause(res.getError());
            finishShard(shardFinishDto, zkJob);

            // callback on fail
            if (job instanceof JobResultListener){
                ((JobResultListener)job).onFail();
            }

        } else if(res.is(JobResult.LATER)){
            // later
            // return the shard to server, and pulled by other clients
            returnShard(instanceId, context.getShardId(), zkJob);
        }
    }

    private JobResult executeScript(JobContext context) {

        String cmd = context.getJobParam();

        Map<String, String> env = Maps.newHashMapWithExpectedSize(2);
        env.put(SCRIPT_JOB_ENV_SHARD_ITEM, context.getShardId() + "");
        if (Strings.isNullOrEmpty(context.getShardParam())){
            env.put(SCRIPT_JOB_ENV_SHARD_PARAM, context.getShardParam());
        }

        return scriptExecutor.exec(cmd, env);
    }

    private JobContext buildJobContext(Long instanceId, PullShard shard) {

        JobContext context = new JobContextImpl();

        context.setInstanceId(instanceId);
        context.setShardId(shard.getId());
        context.setShardParam(shard.getParam());
        context.setShardItem(shard.getItem());
        context.setJobParam(shard.getJobParam());
        context.setTotalShardCount(shard.getTotalShardCount());

        return context;
    }

    private ShardFinishDto buildShardFinishDto(Long instanceId, Long shardId, Date startTime, Date endTime) {

        ShardFinishDto shardFinishDto = new ShardFinishDto();

        shardFinishDto.setInstanceId(instanceId);
        shardFinishDto.setShardId(shardId);
        shardFinishDto.setClient(Systems.hostPid());
        shardFinishDto.setStartTime(startTime);
        shardFinishDto.setEndTime(endTime);

        return shardFinishDto;
    }

    protected void checkInvalidInstance(Long instanceId, ZkJob zkJob, ShardOperateRespCode code) {
        if (code != null){

            if (ShardOperateRespCode.needCleanJobInstance(code)){
                // clean the dirty zk job instance
                String jobInstancePath = ZkPaths.pathOfJobInstance(client.getAppName(), zkJob.getJobClass(), instanceId);
                client.getZk().deleteIfExists(jobInstancePath);
            }
        }
    }

    /**
     * Pull an available shard to convert to the job context
     * @param instanceId the job instance id
     * @param zkJob the zk job
     * @return the job context, the job won't execute if return null
     */
    protected abstract PullShard pullShard(Long instanceId, ZkJob zkJob);

    /**
     * Push the shard for pulling later
     * @param instanceId the job instance id
     * @param shardId the shard id
     * @param zkJob the zk job
     * @return return true if push successfully, or false
     */
    protected abstract Boolean returnShard(Long instanceId, Long shardId, ZkJob zkJob);

    /**
     * Finish the shard
     * @param shardFinishDto the finish shard dto
     * @param zkJob the zk job
     * @return return true if finish successfully, or false
     */
    protected abstract Boolean finishShard(ShardFinishDto shardFinishDto, ZkJob zkJob);
}
