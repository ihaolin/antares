package me.hao0.antares.common.support;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface Lifecycle {

    void start();

    boolean isStart();

    void shutdown();

    boolean isShutdown();
}
