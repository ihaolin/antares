package me.hao0.antares.server.cluster.client;

import me.hao0.antares.common.log.Logs;
import me.hao0.antares.common.support.Lifecycle;
import me.hao0.antares.common.support.Component;
import me.hao0.antares.store.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@org.springframework.stereotype.Component
public class ClientFailover extends Component implements Lifecycle {

    @Autowired
    private ClientCluster clientCluster;

    @Autowired
    private JobService jobService;

    private static final String CLIENT_LISTENER_ID = "me.hao0.antares.server.cluster.client.ClientFailover";

    @Override
    public void doStart() {
        clientCluster.addListener(CLIENT_LISTENER_ID, new AppClientChangedListener() {
            @Override
            public void onChanged(String appName, String client, Boolean join) {
                if(!join){
                    Logs.warn("The app({})'s client({}) left, will do failover.", appName, client);
                    // the client is left
                    doFailover(client);
                }
            }
        });
    }

    private void doFailover(String client) {
        // push back the client's all running shards
        jobService.returnJobInstanceShardsOfClient(client);
    }

    @Override
    public void doShutdown() {
        clientCluster.removeListener(CLIENT_LISTENER_ID);
    }
}
