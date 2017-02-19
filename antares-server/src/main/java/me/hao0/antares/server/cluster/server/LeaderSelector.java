package me.hao0.antares.server.cluster.server;

import com.google.common.base.Objects;
import me.hao0.antares.common.log.Logs;
import me.hao0.antares.common.support.Lifecycle;
import me.hao0.antares.common.support.Component;
import me.hao0.antares.common.util.ZkPaths;
import me.hao0.antares.common.zk.Leader;
import me.hao0.antares.common.zk.LeaderListener;
import me.hao0.antares.server.cluster.client.ClientFailover;
import me.hao0.antares.store.support.AntaresZkClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@org.springframework.stereotype.Component
public class LeaderSelector extends Component implements Lifecycle, InitializingBean, DisposableBean {

    @Autowired
    private AntaresZkClient zk;

    @Autowired
    private ServerHost host;

    @Autowired
    private ServerFailover serverFailover;

    @Autowired
    private ClientFailover clientFailover;

    @Autowired
    private ZkCleaner zkCleaner;

    private Leader leader;

    private ScheduledExecutorService scheduler;

    @Override
    public void doStart(){
        leader = zk.client().acquireLeader(host.get(), ZkPaths.LEADER, new LeaderListener() {
            @Override
            public void isLeader() {

                Logs.info("The server {} become the leader.", host.get());

                // start the client failover
                clientFailover.start();

                // start the zk cleaner
                zkCleaner.start();

                // set the lead's host
                zk.client().update(ZkPaths.LEADER, host.get());

                // start the leader check task
                // avoid that the leader disconnect, because of network jitter
                scheduler = Executors.newScheduledThreadPool(1);
                scheduler.scheduleAtFixedRate(new LeaderCheckTask(), 1, 5, TimeUnit.SECONDS);
            }
        });

        // start the server failover with each server
        // avoid that the leader crashed and can't failover its jobs
        serverFailover.start();
    }

    @Override
    public void doShutdown() {

        clientFailover.shutdown();

        serverFailover.shutdown();

        zkCleaner.shutdown();

        leader.release();

        if (scheduler != null){
            scheduler.shutdown();
        }
    }

    /**
     * Get the current leader host
     * @return the current leader host
     */
    public String getLeader(){
        return leader.getLeader();
    }

    /**
     * Is the leader or not
     * @return return true if I'm the leader, or false
     */
    public Boolean isLeader(){
        return leader.isLeader();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    @Override
    public void destroy() throws Exception {
        shutdown();
    }

    private class LeaderCheckTask implements Runnable{

        @Override
        public void run() {
            if (!Objects.equal(host.get(), getLeader())){

                Logs.warn("The server({}) isn't the leader, maybe disconnect unexpectedly.", host.get());

                // shutdown the client failover
                clientFailover.shutdown();

                // shutdown the zk cleaner
                zkCleaner.shutdown();

                // require the leader
                leader.reaquireLeader();

                // shutdown the scheduler
                scheduler.shutdown();
            }
        }
    }
}
