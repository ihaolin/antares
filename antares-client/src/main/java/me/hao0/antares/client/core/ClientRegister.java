package me.hao0.antares.client.core;

import me.hao0.antares.common.support.Lifecycle;
import me.hao0.antares.common.support.Component;
import me.hao0.antares.common.util.Systems;
import me.hao0.antares.common.util.ZkPaths;
import me.hao0.antares.common.zk.ZkClient;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class ClientRegister extends Component implements Lifecycle {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final AbstractAntaresClient client;

    public ClientRegister(AbstractAntaresClient client) {
        this.client = client;
    }

    @Override
    public void doStart() {

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                ZkClient zk = client.getZk();

                // register period, prevent client disconnects unexpected
                String appClientPath = ZkPaths.pathOfAppClient(client.getAppName(), Systems.hostPid());
                if (!zk.checkExists(appClientPath)){
                    zk.createEphemeral(appClientPath);
                }

            }
        }, 1, 10, TimeUnit.SECONDS);
    }

    @Override
    public void doShutdown() {
        scheduler.shutdown();
    }
}
