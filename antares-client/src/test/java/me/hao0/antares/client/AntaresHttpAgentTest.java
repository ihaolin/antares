package me.hao0.antares.client;

import com.google.common.base.Predicate;
import me.hao0.antares.client.core.AntaresHttpAgent;
import me.hao0.antares.common.dto.ShardFinishDto;
import me.hao0.antares.common.dto.ShardOperateResp;
import me.hao0.antares.common.dto.ShardPullResp;
import me.hao0.antares.common.model.enums.ShardOperateRespCode;
import me.hao0.antares.common.retry.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class AntaresHttpAgentTest {

    private AntaresHttpAgent http;

    @Before
    public void init(){
        http = new AntaresHttpAgent(null);
        http.setCurrentServer("localhost:22122");
    }

    @Test
    public void testPullShard(){
        ShardPullResp pullShardResp = http.pullJobInstanceShard(1L);
        assertNotNull(pullShardResp);
        assertNotNull(pullShardResp.getPullShard());
        System.out.println(pullShardResp.getPullShard());
    }

    @Test
    public void testFinishShard(){
        ShardFinishDto shardFinishDto = new ShardFinishDto();
        shardFinishDto.setClient("10.75.168.128:72567");
        shardFinishDto.setInstanceId(4L);
        shardFinishDto.setShardId(16L);
        shardFinishDto.setStartTime(new Date());
        shardFinishDto.setEndTime(new Date());
        ShardOperateResp shardFinishResp = http.finishJobInstanceShard(shardFinishDto);
        assertNotNull(shardFinishResp);
    }

    @Test
    public void testPullShardRetry(){

        final Long instanceId = 1L;

        Callable<ShardPullResp> pullingShard = new Callable<ShardPullResp>() {
            public ShardPullResp call() throws Exception {
                return http.pullJobInstanceShard(instanceId);
            }
        };

        Retryer<ShardPullResp> retryer = RetryerBuilder.<ShardPullResp>newBuilder()
                .retryIfResult(new Predicate<ShardPullResp>() {
                    @Override
                    public boolean apply(ShardPullResp shardPullResp) {
                        return ShardOperateRespCode.needPullAgain(shardPullResp.getCode());
                    }
                })
                .withStopStrategy(StopStrategies.neverStop())
                .withWaitStrategy(WaitStrategies.fixedWait(3, TimeUnit.SECONDS))
                /*.withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        System.out.println("it will retry: " + attempt.getResult());
                    }
                })*/
                .build();

        try {
            ShardPullResp shardPullResp = retryer.call(pullingShard);
            System.out.println(shardPullResp);
        } catch (ExecutionException e) {
            // e.printStackTrace();
        } catch (RetryException e) {
            // e.printStackTrace();
        }
    }
}
