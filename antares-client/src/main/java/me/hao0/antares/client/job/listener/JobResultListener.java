package me.hao0.antares.client.job.listener;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface JobResultListener {

    /**
     * Callback when job execute successfully
     */
    void onSuccess();

    /**
     * Callback when job execute failed
     */
    void onFail();
}
