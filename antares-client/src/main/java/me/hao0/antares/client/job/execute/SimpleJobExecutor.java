package me.hao0.antares.client.job.execute;

import me.hao0.antares.client.core.AntaresClient;
import me.hao0.antares.common.dto.PullShard;
import me.hao0.antares.common.dto.ShardFinishDto;
import me.hao0.antares.common.dto.ShardOperateResp;
import me.hao0.antares.common.dto.ShardPullResp;
import me.hao0.antares.common.model.enums.ShardOperateRespCode;
import me.hao0.antares.common.util.Sleeps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The pull job executor
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class SimpleJobExecutor extends AbstractJobExecutor implements JobExecutor {

    private Logger log = LoggerFactory.getLogger(SimpleJobExecutor.class);

    private static final Integer RETRY_INTERVAL = 5;

    public SimpleJobExecutor(AntaresClient client) {
        super(client);
    }

    @Override
    protected PullShard pullShard(Long instanceId, final ZkJob zkJob) {

        ShardPullResp pullResp;

        for(;;){

            pullResp = client.getHttp().pullJobInstanceShard(instanceId);
            if (pullResp == null){
                return null;
            }

            if (ShardOperateRespCode.needPullAgain(pullResp.getCode())){
                log.info("retry to pull shard(job={}, instanceId={}), resp={}",
                        zkJob.getJob(), instanceId, pullResp);
                Sleeps.sleep(RETRY_INTERVAL);
                continue;
            }

            checkInvalidInstance(instanceId, zkJob, pullResp.getCode());

            return pullResp.getPullShard();
        }
    }

    @Override
    protected Boolean returnShard(final Long instanceId, final Long shardId, final ZkJob zkJob) {

        ShardOperateResp returnResp;

        for(;;){

            returnResp = client.getHttp().returnJobInstanceShard(instanceId, shardId);
            if (returnResp == null){
                return null;
            }

            if (ShardOperateRespCode.needReturnAgain(returnResp.getCode())){
                log.info("retry to push shard(job={}, instanceId={}, shardId={}), resp={}",
                        instanceId, zkJob.getJob(), shardId, returnResp);
                Sleeps.sleep(RETRY_INTERVAL);
                continue;
            }

            checkInvalidInstance(instanceId, zkJob, returnResp.getCode());

            return returnResp.getSuccess();
        }
    }

    @Override
    protected Boolean finishShard(final ShardFinishDto shardFinishDto, final ZkJob zkJob) {

        ShardOperateResp finishResp;

        for(;;){

            finishResp = client.getHttp().finishJobInstanceShard(shardFinishDto);
            if (finishResp == null){
                return null;
            }

            if (ShardOperateRespCode.needFinishAgain(finishResp.getCode())){
                log.info("retry to finish shard(job={}, shardFinishDto={}), resp={}.", shardFinishDto, zkJob.getJob(), finishResp);
                Sleeps.sleep(RETRY_INTERVAL);
                continue;
            }

            checkInvalidInstance(shardFinishDto.getInstanceId(), zkJob, finishResp.getCode());

            return finishResp.getSuccess();
        }
    }
}
