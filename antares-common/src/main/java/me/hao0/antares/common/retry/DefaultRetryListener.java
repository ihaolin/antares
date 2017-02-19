package me.hao0.antares.common.retry;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutionException;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class DefaultRetryListener implements RetryListener {

    private static final Logger log = LoggerFactory.getLogger(DefaultRetryListener.class);

    @Override
    public <V> void onRetry(Attempt<V> attempt) {
        try {
            long attemptTimes = attempt.getAttemptNumber();
            if (attemptTimes > 2 && attemptTimes < 10){
                log.info("try the {} times, and result is: {}", attempt.getAttemptNumber(), attempt.get());
            } else if(attemptTimes > 10) {
                log.warn("try the {} times, and result is: {}", attempt.getAttemptNumber(), attempt.get());
            }
        } catch (ExecutionException e) {
            // ignore
            log.error("failed to on retry, cause: {}", Throwables.getStackTraceAsString(e));
        }
    }
}
