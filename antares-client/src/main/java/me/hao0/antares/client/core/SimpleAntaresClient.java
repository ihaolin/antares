package me.hao0.antares.client.core;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class SimpleAntaresClient extends AbstractAntaresClient implements AntaresClient {

    public SimpleAntaresClient(String appName, String zkServers) {
        super(appName, zkServers);
    }

    public SimpleAntaresClient(String appName, String appSecret, String zkServers) {
        super(appName, appSecret, zkServers);
    }

    public SimpleAntaresClient(String appName, String appSecret, String zkServers, String zkNamespace) {
        super(appName, appSecret, zkServers, zkNamespace);
    }

    @Override
    protected void afterStart() {

    }

    @Override
    protected void afterShutdown() {

    }
}
