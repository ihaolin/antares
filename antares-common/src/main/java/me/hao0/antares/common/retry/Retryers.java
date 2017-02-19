package me.hao0.antares.common.retry;

import com.google.common.base.Predicate;
import java.util.concurrent.TimeUnit;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public final class Retryers {

    private Retryers(){}

    private static class RetryersHolder{
        static Retryers INSTANCE = new Retryers();
    }

    public static Retryers get(){
        return RetryersHolder.INSTANCE;
    }

    /**
     * New a retryer
     * @param p predicate that whether retry or not
     * @param <T> the generic type
     * @return the retryer
     */
    public <T> Retryer<T> newRetryer(Predicate<T> p){
        return newRetryer(p, 3, -1, null);
    }

    /**
     * New a retryer
     * @param p predicate that whether retry or not
     * @param <T> the generic type
     * @return the retryer
     */
    public <T> Retryer<T> newRetryer(Predicate<T> p, int fixWaitSecs){
        return newRetryer(p, fixWaitSecs, -1,  null);
    }

    /**
     * New a retryer
     * @param p predicate that whether retry or not
     * @param fixWaitSecs the fixed wait seconds per retry
     * @param attemptTimes the times for retrying
     * @param <T> the generic type
     * @return the retryer
     */
    public <T> Retryer<T> newRetryer(Predicate<T> p, int fixWaitSecs, int attemptTimes, RetryListener retryListener){

        RetryerBuilder<T> builder =  RetryerBuilder.<T>newBuilder()
                .retryIfResult(p)
                .retryIfRuntimeException()
                .withWaitStrategy(WaitStrategies.fixedWait(fixWaitSecs, TimeUnit.SECONDS));

        // attempt times
        if (attemptTimes > 0){
            builder.withStopStrategy(StopStrategies.stopAfterAttempt(attemptTimes));
        } else {
            builder.withStopStrategy(StopStrategies.neverStop());
        }

        // listener
        if (retryListener == null){
            retryListener = new DefaultRetryListener();
        }
        builder.withRetryListener(retryListener);

        return builder.build();
    }
}
