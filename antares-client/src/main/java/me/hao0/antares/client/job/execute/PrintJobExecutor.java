package me.hao0.antares.client.job.execute;

import me.hao0.antares.client.core.AntaresClient;
import me.hao0.antares.client.job.JobContext;
import me.hao0.antares.common.dto.PullShard;
import me.hao0.antares.common.dto.ShardFinishDto;
import me.hao0.antares.common.dto.ShardOperateResp;
import me.hao0.antares.common.model.enums.ShardOperateRespCode;

/**
 * The print job executor for debug
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class PrintJobExecutor extends AbstractJobExecutor implements JobExecutor {

    public PrintJobExecutor(AntaresClient client) {
        super(client);
    }

    @Override
    protected PullShard pullShard(Long instanceId, ZkJob zkJob) {

        System.out.println(zkJob.getJob().getClass().getName() + " is fired.");

        return null;
    }

    @Override
    protected Boolean returnShard(Long instanceId, Long shardId, ZkJob zkJob) {
        return null;
    }

    @Override
    protected Boolean finishShard(ShardFinishDto shardFinishDto, ZkJob zkJob) {

        ShardOperateResp finishResp = client.getHttp().finishJobInstanceShard(shardFinishDto);

        if (finishResp.getSuccess()){
            return Boolean.TRUE;
        }

        if (ShardOperateRespCode.needFinishAgain(finishResp.getCode())){
            // TODO need retry to finish again
        }

        return Boolean.TRUE;
    }
}
