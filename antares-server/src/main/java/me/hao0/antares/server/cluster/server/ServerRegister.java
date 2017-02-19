package me.hao0.antares.server.cluster.server;

import me.hao0.antares.common.log.Logs;
import me.hao0.antares.common.support.Lifecycle;
import me.hao0.antares.common.support.Component;
import me.hao0.antares.common.util.ZkPaths;
import me.hao0.antares.store.support.AntaresZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@org.springframework.stereotype.Component
@Lazy
public class ServerRegister extends Component implements Lifecycle{

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    private ServerHost serverHost;

    @Autowired
    private AntaresZkClient zk;

    @Override
    public void doStart() {

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                String server = serverHost.get();

                // mkdirs /cluster/servers if necessary
                zk.client().mkdirs(ZkPaths.SERVERS);

                // register the server node
                String serverPath = ZkPaths.pathOfServer(server);

                if (!zk.client().checkExists(serverPath)){
                    String result = zk.client().createEphemeral(ZkPaths.pathOfServer(server));
                    Logs.info("server({}) registered: {}", server, result);
                }
            }
        }, 1, 5, TimeUnit.SECONDS);

    }

    @Override
    public void doShutdown() {
        scheduler.shutdown();
    }
}
