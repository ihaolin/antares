package me.hao0.antares.server.cluster.server;

import me.hao0.antares.common.exception.ZkException;
import me.hao0.antares.common.support.Lifecycle;
import me.hao0.antares.common.support.Component;
import me.hao0.antares.common.util.ZkPaths;
import me.hao0.antares.common.zk.Lock;
import me.hao0.antares.store.support.AntaresZkClient;
import org.apache.curator.framework.recipes.locks.ChildReaper;
import org.apache.curator.framework.recipes.locks.Reaper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@org.springframework.stereotype.Component
public class ZkCleaner extends Component implements Lifecycle, DisposableBean {

    @Autowired
    private AntaresZkClient zk;

    private ChildReaper emptyChildCleaner;

    @Override
    public void doStart() {
        String jobInstancesLockPath = Lock.PREFIX + ZkPaths.JOB_INSTANCES;
        zk.client().mkdirs(jobInstancesLockPath);
        emptyChildCleaner = new ChildReaper(zk.client().client(), jobInstancesLockPath, Reaper.Mode.REAP_INDEFINITELY);
        try {

            String serversFailover = Lock.PREFIX + ZkPaths.SERVER_FAILOVER;
            zk.client().mkdirs(serversFailover);
            emptyChildCleaner.addPath(serversFailover);

            emptyChildCleaner.start();
        } catch (Exception e) {
            throw new ZkException(e);
        }
    }

    @Override
    public void doShutdown() {
        if (emptyChildCleaner != null){
            try {
                emptyChildCleaner.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        shutdown();
    }
}
