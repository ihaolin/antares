package me.hao0.antares.common.zk;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import me.hao0.antares.common.exception.ZkException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.EnsurePath;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Author: haolin
 * Email : haolin.h0@gmail.com
 */
public class ZkClient {

    private static final Logger log = LoggerFactory.getLogger(ZkClient.class);

    private static final ExponentialBackoffRetry DEFAULT_RETRY_STRATEGY = new ExponentialBackoffRetry(1000, 3);

    private final String hosts;

    private final String namespace;

    private final ExponentialBackoffRetry retryStrategy;

    private CuratorFramework client;

    private volatile boolean started;

    private final java.util.concurrent.locks.Lock RESTART_LOCK = new ReentrantLock();

    private ZkClient(String hosts, String namespace, ExponentialBackoffRetry retryStrategy){
        this.hosts = hosts;
        this.namespace = namespace;
        this.retryStrategy = retryStrategy;
    }

    /**
     * Create a client instancclientAppPathExiste
     * @param hosts host strings: zk01:2181,zk02:2181,zk03:2181
     * @param namespace path root, such as app name
     */
    public static ZkClient newClient(String hosts, String namespace){
        return newClient(hosts, namespace, DEFAULT_RETRY_STRATEGY);
    }

    /**
     * Create a client instance
     * @param hosts host strings: zk01:2181,zk02:2181,zk03:2181
     * @param namespace path root, such as app name
     * @param retryStrategy client retry strategy
     */
    public static ZkClient newClient(String hosts, String namespace, ExponentialBackoffRetry retryStrategy){
        ZkClient zc = new ZkClient(hosts, namespace, retryStrategy);
        zc.start();
        return zc;
    }

    private void start() {

        if (started){
            return;
        }

        doStart();
    }

    private void doStart(){

        client = CuratorFrameworkFactory.builder()
                    .connectString(hosts)
                    .namespace(namespace)
                    .retryPolicy(retryStrategy)
                    .build();

        client.start();

        try {
            // connected until
            client.blockUntilConnected(30, TimeUnit.SECONDS);
            started = true;
        } catch (InterruptedException e) {
            throw new ZkException(e);
        }
    }

    public void restart(){

        try {

            boolean locked = RESTART_LOCK.tryLock(30, TimeUnit.SECONDS);
            if (!locked){
                log.warn("timeout to get the restart lock, maybe it's locked by another.");
                return;
            }

            if (client.getZookeeperClient().isConnected()){
                return;
            }

            if (client != null){
                // close old connection
                client.close();
            }

            doStart();

        } catch (InterruptedException e) {
            log.error("failed to get the restart lock, cause: {}", Throwables.getStackTraceAsString(e));
        } finally {
            RESTART_LOCK.unlock();
        }

    }

    /**
     * Get the inner curator client
     * @return the inner curator client
     */
    public CuratorFramework client(){
        return client;
    }

    /**
     * Shutdown the client
     */
    public void shutdown(){
        if (client != null){
            client.close();
            started = false;
        }
    }

    /**
     * Create an persistent path
     * @param path path
     * @return the path created
     */
    public String create(String path) {
        return create(path, (byte[])null);
    }

    /**
     * Create an persistent path
     * @param path path
     * @param data byte data
     * @return the path created
     */
    public String create(String path, byte[] data) {
        try {
            return client.create().withMode(CreateMode.PERSISTENT).forPath(path, data);
        } catch (Exception e) {
            handleConnectionLoss(e);
            throw new ZkException(e);
        }
    }

    /**
     * Create an persistent path
     * @param path path
     * @param data string data
     * @return the path created
     */
    public String create(String path, String data){
        try {
            return create(path, data.getBytes("UTF-8"));
        } catch (Exception e) {
            handleConnectionLoss(e);
            throw new ZkException(e);
        }
    }

    /**
     * Create an persistent path, save the object to json
     * @param path path
     * @param obj object
     * @return the path created
     */
    public String create(String path, Object obj){
        return create(path, JSON.toJSONString(obj));
    }

    /**
     * Create an persistent path
     * @param path path
     * @param data byte data
     * @return the path created
     */
    public String createSequential(String path, byte[] data) {
        try {
            return client.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(path, data);
        } catch (Exception e) {
            handleConnectionLoss(e);
            throw new ZkException(e);
        }
    }

    /**
     * Create an persistent path
     * @param path path
     * @param data byte data
     * @return the path created
     */
    public String createSequential(String path, String data) {
        try {
            return createSequential(path, data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            handleConnectionLoss(e);
            throw new ZkException(e);
        }
    }

    /**
     * Create an persistent path
     * @param path path
     * @param obj a object
     * @return the path created
     */
    public String createSequentialJson(String path, Object obj) {
        try {
            return createSequential(path, JSON.toJSONString(obj).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            handleConnectionLoss(e);
            throw new ZkException(e);
        }
    }


    /**
     * Create an ephemeral path
     * @param path path
     * @return the path created
     */
    public String createEphemeral(String path) {
        return createEphemeral(path, (byte[]) null);
    }

    /**
     * Create an ephemeral path
     * @param path path
     * @param data byte data
     * @return the path created
     */
    public String createEphemeral(String path, byte[] data) {
        try {
            return client.create().withMode(CreateMode.EPHEMERAL).forPath(path, data);
        } catch (Exception e) {
            handleConnectionLoss(e);
            throw new ZkException(e);
        }
    }

    /**
     * Create an ephemeral path
     * @param path path
     * @param data string data
     * @return the path created
     */
    public String createEphemeral(String path, String data){
        try {
            return client.create().withMode(CreateMode.EPHEMERAL).forPath(path, data.getBytes("UTF-8"));
        } catch (Exception e) {
            handleConnectionLoss(e);
            throw new ZkException(e);
        }
    }

    /**
     * Create an ephemeral path
     * @param path path
     * @param data data
     * @return the path created
     */
    public String createEphemeral(String path, Integer data) {
        return createEphemeral(path, data.toString());
    }

    /**
     * Create an ephemeral path
     * @param path path
     * @param obj object data
     * @return the path created
     */
    public String createEphemeral(String path, Object obj) {
        return createEphemeral(path, JSON.toJSONString(obj));
    }

    /**
     * Create an ephemeral path
     * @param path path
     * @param data byte data
     * @return the path created
     * @throws Exception
     */
    public String createEphemeralSequential(String path, byte[] data) {
        try {
            return client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path, data);
        } catch (Exception e) {
            handleConnectionLoss(e);
            throw new ZkException(e);
        }
    }

    /**
     * Create an ephemeral path
     * @param path path
     * @param data string data
     * @return the path created
     * @throws Exception
     */
    public String createEphemeralSequential(String path, String data) {
        try {
            return createEphemeralSequential(path, data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            handleConnectionLoss(e);
            throw new ZkException(e);
        }
    }

    /**
     * Create an ephemeral and sequential path
     * @param path path
     * @param obj object
     * @return the path created
     * @throws Exception
     */
    public String createEphemeralSequential(String path, Object obj) {
        return createEphemeralSequential(path, JSON.toJSONString(obj));
    }

    /**
     * Create a node if not exists
     * @param path path
     * @param data path data
     * @return return true if create
     * @throws Exception
     */
    public Boolean createIfNotExists(String path, String data) {
        try {
            return createIfNotExists(path, data.getBytes("UTF-8"));
        } catch (Exception e) {
            handleConnectionLoss(e);
            throw new ZkException(e);
        }
    }

    /**
     * Create a node if not exists
     * @param path path
     * @return return true if create
     */
    public Boolean createIfNotExists(String path) {
        return createIfNotExists(path, (byte[])null);
    }

    /**
     * Create a node if not exists
     * @param path path
     * @param data path data
     * @return return true if create
     */
    public Boolean createIfNotExists(String path, byte[] data) {
        try {
            Stat pathStat = client.checkExists().forPath(path);
            if (pathStat == null){
                String nodePath = client.create().forPath(path, data);
                return Strings.isNullOrEmpty(nodePath) ? Boolean.FALSE : Boolean.TRUE;
            }
        } catch (Exception e) {
            handleConnectionLoss(e);
            throw new ZkException(e);
        }

        return Boolean.FALSE;
    }

    /**
     * Check the path exists or not
     * @param path the path
     * @return return true if the path exists, or false
     */
    public Boolean checkExists(String path){
        try {
            Stat pathStat = client.checkExists().forPath(path);
            return pathStat != null;
        } catch (Exception e) {
            handleConnectionLoss(e);
            throw new ZkException(e);
        }
    }

    /**
     * Make directories if necessary
     * @param dir the dir
     * @return return true if mkdirs successfully, or throw ZkException
     */
    public Boolean mkdirs(String dir){
        try {
            EnsurePath clientAppPathExist =
                    new EnsurePath("/" + client.getNamespace() + slash(dir));
            clientAppPathExist.ensure(client.getZookeeperClient());
            return Boolean.TRUE;
        } catch (Exception e) {
            handleConnectionLoss(e);
            throw new ZkException(e);
        }
    }

    public Boolean update(String path, Integer data){
        return update(path, data.toString());
    }

    public Boolean update(String path, Object data){
        return update(path, JSON.toJSONString(data));
    }

    public Boolean update(String path, String data){
        try {
            return update(path, data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new ZkException(e);
        }
    }

    public Boolean update(String path){
        return update(path, (byte[])null);
    }

    public Boolean update(String path, byte[] data){
        try {
            client.setData().forPath(path, data);
            return Boolean.TRUE;
        } catch (Exception e) {
            handleConnectionLoss(e);
            throw new ZkException(e);
        }
    }

    /**
     * Delete the node
     * @param path node path
     */
    public void delete(String path) {
        try {
            client.delete().forPath(path);
        } catch (Exception e){
            handleConnectionLoss(e);
            throw new ZkException(e);
        }
    }

    /**
     * Delete the node if the node exists
     * @param path node path
     */
    public void deleteIfExists(String path) {
        try {
            if(checkExists(path)){
                delete(path);
            }
        } catch (Exception e){
            handleConnectionLoss(e);
            throw new ZkException(e);
        }
    }

    /**
     * Delete the node recursively
     * @param path the node path
     */
    public void deleteRecursively(String path){
        try {
            client.delete().deletingChildrenIfNeeded().forPath(path);
        } catch (Exception e){
            handleConnectionLoss(e);
            throw new ZkException(e);
        }
    }

    /**
     * Delete the node recursively if the path exists
     * @param path the node path
     */
    public void deleteRecursivelyIfExists(String path){
        try {
            if(checkExists(path)){
                deleteRecursively(path);
            }
        } catch (Exception e){
            handleConnectionLoss(e);
            throw new ZkException(e);
        }
    }

    /**
     * get the data of path
     * @param path the node path
     * @return the byte data of the path
     */
    public byte[] get(String path){
        try {
            return client.getData().forPath(path);
        } catch (Exception e){
            handleConnectionLoss(e);
            throw new ZkException(e);
        }
    }

    /**
     * get the node data as string
     * @param path path data
     * @return return the data string or null
     */
    public String getString(String path){
        byte[] data = get(path);
        if (data != null){
            try {
                return new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public Integer getInteger(String nodePath) {
        String nodeValue = getString(nodePath);
        return Strings.isNullOrEmpty(nodeValue) ? null : Integer.parseInt(nodeValue);
    }

    /**
     * get the node data as an object
     * @param path node path
     * @param clazz class
     * @return json object or null
     */
    public <T> T getJson(String path, Class<T> clazz){
        byte[] data = get(path);
        if (data != null){
            try {
                String json = new String(data, "UTF-8");
                return JSON.parseObject(json, clazz);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * Get the children of the path
     * @param path the path
     * @return the children of the path
     */
    public List<String> gets(String path){
        try {

            if (!checkExists(path)){
                return Collections.emptyList();
            }

            return client.getChildren().forPath(path);
        } catch (Exception e) {
            handleConnectionLoss(e);
            throw new ZkException(e);
        }
    }

    private String slash(String path){
        return path.startsWith("/") ? path : "/" + path;
    }

    /**
     * new a watcher of path child
     * @param path the parent path
     * @param listener a listener
     * NOTE:
     *   Only watch first level children, not recursive
     */
    public ChildWatcher newChildWatcher(String path, ChildListener listener) {
        return newChildWatcher(path, listener, Boolean.TRUE);
    }

    /**
     * new a watcher of path child
     * @param path the parent path
     * @param listener a listener
     * @param cacheChildData cache child or not
     *
     * <p>NOTE:
     *   Only watch first level children, not recursive
     * </p>
     * @return the child watcher
     */
    public ChildWatcher newChildWatcher(String path, ChildListener listener, Boolean cacheChildData) {
        return new ChildWatcher(client, path, cacheChildData, listener);
    }

    /**
     * new a node watcher
     * @param nodePath the node path
     * @param listener the node listener
     * @return the node watcher
     */
    public NodeWatcher newNodeWatcher(String nodePath, NodeListener listener){
        return new NodeWatcher(client, nodePath, listener);
    }

    /**
     * new a node watcher
     * @param nodePath the node path
     * @return the node watcher
     */
    public NodeWatcher newNodeWatcher(String nodePath){
        return newNodeWatcher(nodePath, null);
    }

    /**
     * lock the path
     * @param path the path
     */
    public Lock newLock(String path) {
        return new Lock(client, path);
    }

    /**
     * Acquire the leadership
     * @param leaderPath the leader node path
     * @param listener the leader listener
     * @return the leadership
     */
    public Leader acquireLeader(String leaderPath, LeaderListener listener){
        return acquireLeader(null, leaderPath, listener);
    }

    /**
     * Acquire the leadership
     * @param id identify of the current participant
     * @param leaderPath the leader node path
     * @param listener the leader listener
     * @return the leadership
     */
    public Leader acquireLeader(String id, String leaderPath, LeaderListener listener){
        return new Leader(client, id, leaderPath, listener);
    }

    private void handleConnectionLoss(Exception e){
        if (e instanceof KeeperException.ConnectionLossException){

            log.warn("zk client will restart...");

            // try to restart the zk connection
            restart();

            log.warn("zk client do restart finished.");
        }
    }
}