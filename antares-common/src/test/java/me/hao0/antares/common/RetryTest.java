package me.hao0.antares.common;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import me.hao0.antares.common.dto.PullShard;
import me.hao0.antares.common.dto.ShardPullResp;
import me.hao0.antares.common.model.enums.ShardOperateRespCode;
import me.hao0.antares.common.retry.RetryException;
import me.hao0.antares.common.retry.Retryer;
import me.hao0.antares.common.retry.Retryers;
import org.junit.Test;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class RetryTest {

    @Test
    public void testPullShardSuccess() throws ExecutionException, RetryException {
        PullShard pullShard = new PullShard();
        pullShard.setItem(0);
        pullShard.setId(1L);
        final ShardPullResp resp = new ShardPullResp(ShardOperateRespCode.SHARD_PULL_SUCCESS, pullShard);

        Retryer<ShardPullResp> respRetryer = Retryers.get().newRetryer(new Predicate<ShardPullResp>() {
            @Override
            public boolean apply(ShardPullResp r) {
                return ShardOperateRespCode.needPullAgain(r.getCode());
            }
        });

        respRetryer.call(new Callable<ShardPullResp>() {
            @Override
            public ShardPullResp call() throws Exception {
                return resp;
            }
        });
    }

    @Test
    public void testFinishShardRetry() throws ExecutionException, RetryException {
        Retryer<Boolean> falseRetyer = Retryers.get().newRetryer(Predicates.<Boolean>alwaysFalse(), 5);

        falseRetyer.call(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return Boolean.FALSE;
            }
        });
    }
}
