package me.hao0.antares.common;

import me.hao0.antares.common.util.Networks;
import me.hao0.antares.common.util.Systems;
import me.hao0.antares.common.zk.ChildListener;
import me.hao0.antares.common.zk.LeaderListener;
import me.hao0.antares.common.zk.Lock;
import me.hao0.antares.common.zk.NodeListener;
import me.hao0.antares.common.zk.ZkClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class ZkClientTest {

    private ZkClient zk;

    @Before
    public void init(){
        zk = ZkClient.newClient("localhost:2181", "ats");
    }

    @Test
    public void testCreate(){
        zk.create("/persist_node", "This node is persist.");
    }

    @Test
    public void testCreateEphemeral() throws InterruptedException {
        zk.createEphemeral("/ephemeral_node", "This node is ephemeral.");
        Thread.sleep(10000L);
        // The node will be deleted
    }

    @Test
    public void testAcquireLeader() throws InterruptedException {

        final String id = Networks.getSiteIp() + ":" + Systems.pid();

        zk.acquireLeader(id, "/leader", new LeaderListener() {
            @Override
            public void isLeader() {
                System.err.println("I'm the leader now(" + id + ").");
            }
        });

        Thread.sleep(20000L);
    }

    @Test
    public void testWatchNode() throws InterruptedException {
        String nodePath = "/node_watch";
        String nodeData = "Test the node watch";

        zk.createIfNotExists(nodePath, nodeData);

        zk.newNodeWatcher(nodePath, new NodeListener() {
            @Override
            public void onUpdate(byte[] newData) {
                System.out.println("node data updated: " + new String(newData));
            }

            @Override
            public void onDelete() {
                System.out.println("node is deleted");
            }
        });

        Thread.sleep(Integer.MAX_VALUE);
    }

    @Test
    public void testChildWatch() throws InterruptedException {
        zk.newChildWatcher("/child_watch", new ChildListener() {
            @Override
            protected void onAdd(String path, byte[] data) {
                System.err.println("new node " + path + " is add: " + new String(data));
            }

            @Override
            protected void onDelete(String path) {
                System.err.println("path " + path + " is deleted.");
            }

            @Override
            protected void onUpdate(String path, byte[] newData) {
                System.err.println("path " + path + " is updated: " + new String(newData));
            }
        });

        Thread.sleep(Integer.MAX_VALUE);
    }

    @Test
    public void testDeleteRecursively(){
        zk.deleteRecursively("/child_watch");
    }

    @Test
    public void testLock() throws InterruptedException {
        Lock lock = zk.newLock("/test_lock");
        lock.lock();
        try {
            System.err.println("I locked it.");
            Thread.sleep(600000L);
        } finally {
            lock.unlock();
        }
    }

    @Test
    public void testZkClientRestart(){

        zk.get("/jobs");

        zk.restart();

        zk.get("/jobs");
    }

    @After
    public void destroy(){
        if (zk != null){
            zk.shutdown();
        }
    }
}
