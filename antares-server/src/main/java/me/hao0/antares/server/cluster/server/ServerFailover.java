package me.hao0.antares.server.cluster.server;

import me.hao0.antares.common.log.Logs;
import me.hao0.antares.common.support.Lifecycle;
import me.hao0.antares.common.support.Component;
import me.hao0.antares.common.util.CollectionUtil;
import me.hao0.antares.common.util.Sleeps;
import me.hao0.antares.common.util.ZkPaths;
import me.hao0.antares.common.zk.Lock;
import me.hao0.antares.store.service.JobService;
import me.hao0.antares.store.service.ServerService;
import me.hao0.antares.store.support.AntaresZkClient;
import me.hao0.antares.common.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@org.springframework.stereotype.Component
public class ServerFailover extends Component implements Lifecycle{

    @Autowired
    private JobService jobService;

    @Autowired
    private ServerService serverService;

    @Autowired
    private ServerCluster serverCluster;

    @Autowired
    private AntaresZkClient zk;

    @Value("${antares.serverFailoverWaitTime:30}")
    private Integer serverFailoverWaitTime;

    @Override
    public void doStart() {
        serverCluster.addListener(new ServerChangedListener() {
            @Override
            public void onServerChanged(String server, Boolean join) {
                if (!join){
                    // the server left
                    doFailOver(server);
                }
            }
        });
    }

    private void doFailOver(String server) {

        // TODO maybe think that one job fired just now, but the server crashed

        // lock the server's failover
        Lock lock = createServerFailoverLock(server);
        if(!lock.lock(1000)){
            // has one server is doing failover
            return;
        }

        try {

            if (tryWaitServerStart(server)){
                return;
            }

            Logs.warn("The server({}) left, will do failover.", server);

            // load the server's jobs
            Response<List<Long>> jobIdsResp = jobService.findJobIdsByServer(server);
            if (!jobIdsResp.isSuccess()){
                Logs.warn("failed to find the server({})'s job ids, cause: {}", server, jobIdsResp.getErr());
                return;
            }

            List<Long> jobIds = jobIdsResp.getData();
            if (CollectionUtil.isNullOrEmpty(jobIds)){
                // don't need to dispatch
                return;
            }

            // dispatch the jobs to other servers
            serverService.scheduleJobs(jobIds, serverCluster.alives());

        } finally {
            lock.unlock();
        }
    }

    /**
     * Try to wait server to start
     * @param server the server
     * @return return true if server started, or false
     */
    private Boolean tryWaitServerStart(String server) {

        Sleeps.sleep(serverFailoverWaitTime);

        // check server register?
        String serverPath = ZkPaths.pathOfServer(server);
        return zk.client().checkExists(serverPath);
    }

    private Lock createServerFailoverLock(String server) {
        String serverFailoverLock = ZkPaths.pathOfServerFailoverLock(server);
        return zk.client().newLock(serverFailoverLock);
    }

    @Override
    public void doShutdown() {

    }
}
