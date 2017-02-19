package me.hao0.antares.common.support;

/**
 * A lifecycle component can reuse
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public abstract class Component implements Lifecycle {

    protected volatile boolean started;

    protected volatile boolean shutdowned;

    @Override
    public boolean isStart() {
        return started;
    }

    @Override
    public boolean isShutdown() {
        return shutdowned;
    }

    @Override
    public void start() {

        if(isStart()){
            return;
        }

        doStart();

        started = true;
        shutdowned = false;
    }

    protected abstract void doStart();

    @Override
    public void shutdown() {

        if (isShutdown()){
            return;
        }

        doShutdown();

        shutdowned = true;
        started = false;
    }

    protected abstract void doShutdown();
}
