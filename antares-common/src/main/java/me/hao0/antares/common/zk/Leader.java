package me.hao0.antares.common.zk;

import me.hao0.antares.common.exception.ZkException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.recipes.leader.Participant;
import java.util.concurrent.CountDownLatch;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class Leader {

    private final LeaderSelector selector;

    private CountDownLatch latch;

    Leader(CuratorFramework client, String leaderPath, final LeaderListener listener) {
        this(client, null, leaderPath, listener);
    }

    Leader(CuratorFramework client, String id, String leaderPath, final LeaderListener listener) {
        selector = new LeaderSelector(client, leaderPath, new LeaderSelectorListenerAdapter() {
            @Override
            public void takeLeadership(CuratorFramework client) throws Exception {

                latch = new CountDownLatch(1);

                listener.isLeader();

                // it will release the leadership if return;
                latch.await();
            }
        });

        if (id != null){
            selector.setId(id);
        }

        selector.start();
    }

    /**
     * Am I the leader or not
     * @return return true if I am the leader, or false
     */
    public Boolean isLeader(){
        return selector.hasLeadership();
    }

    /**
     * Get the current leader
     * @return the leader's id, or null if the leader doesn't exist
     */
    public String getLeader(){
        try {
            Participant p = selector.getLeader();
            if (p != null){
                return p.getId();
            }
        } catch (Exception e) {
            throw new ZkException(e);
        }
        return null;
    }

    /**
     * Require the leadership
     */
    public Boolean reaquireLeader(){
        return selector.requeue();
    }

    /**
     * Release the leadership manually
     */
    public void release(){
        if (latch != null){
            latch.countDown();
        }
    }
}
