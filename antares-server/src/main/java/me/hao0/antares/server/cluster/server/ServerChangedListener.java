package me.hao0.antares.server.cluster.server;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface ServerChangedListener {

    /**
     * Callback when a server is joined or left
     * @param server the server host:port
     * @param join true is joined, false is left
     */
    void onServerChanged(String server, Boolean join);
}
