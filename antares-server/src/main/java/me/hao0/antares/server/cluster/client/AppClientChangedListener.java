package me.hao0.antares.server.cluster.client;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface AppClientChangedListener {

    /**
     * Callback when the app's clients changed
     * @param appName the app name
     * @param client the client host
     * @param join true is join, false is left
     */
    void onChanged(String appName, String client, Boolean join);
}
