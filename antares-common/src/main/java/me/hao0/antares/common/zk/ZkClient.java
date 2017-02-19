package me.hao0.antares.common.zk;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import me.hao0.antares.common.exception.ZkException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.EnsurePath;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Author: haolin
 * Email : haolin.h0@gmail.com
 */
public class ZkClient {

    private static final ExponentialBackoffRetry DEFAULT_RETRY_STRATEGY = new ExponentialBackoffRetry(1000, 3);

    private CuratorFramework client;

    private ZkClient(){}

    /**
     * Create a client instancclientAppPathExiste
     * @param hosts host strings: zk01:2181,zk02:2181,zk03:2181
     * @param namespace path root, such as app name
     */
    public static ZkClient newClient(String hosts, String namespace){
        return newClient(hosts, DEFAULT_RETRY_STRATEGY, namespace);
    }

    /**
     * Create a client instance
     * @param hosts host strings: zk01:2181,zk02:2181,zk03:2181
     * @param namespace path root, such as app name
     * @param retryStrategy client retry strategy
     */
    public static ZkClient newClient(String hosts, ExponentialBackoffRetry retryStrategy, String namespace){
        ZkClient zc = new ZkClient();
        zc.client = CuratorFrameworkFactory.builder()
                        .connectString(hosts).retryPolicy(retryStrategy).namespace(namespace).build();
        zc.client.start();
        return zc;
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
        }
    }

    /**
     * Create an persistent path
     * @param path path
     * @return the path created
     * @throws Exception
     */
    public String create(String path) {
        return create(path, (byte[])null);
    }

    /**
     * Create an persistent path
     * @param path path
     * @param data byte data
     * @return the path created
     * @throws Exception
     */
    public String create(String path, byte[] data) {
        try {
            return client.create().withMode(CreateMode.PERSISTENT).forPath(path, data);
        } catch (Exception e) {
            throw new ZkException(e);
        }
    }

    /**
     * Create an persistent path
     * @param path path
     * @param data string data
     * @return the path created
     * @throws Exception
     */
    public String create(String path, String data){
        try {
            return create(path, data.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new ZkException(e);
        }
    }

    /**
     * Create an persistent path, save the object to json
     * @param path path
     * @param obj object
     * @return the path created
     * @throws Exception
     */
    public String create(String path, Object obj){
        return create(path, JSON.toJSONString(obj));
    }

    /**
     * Create an persistent path
     * @param path path
     * @param data byte data
     * @return the path created
     * @throws Exception
     */
    public String createSequential(String path, byte[] data) {
        try {
            return client.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(path, data);
        } catch (Exception e) {
            throw new ZkException(e);
        }
    }

    /**
     * Create an persistent path
     * @param path path
     * @param data byte data
     * @return the path created
     * @throws Exception
     */
    public String createSequential(String path, String data) {
        try {
            return createSequential(path, data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new ZkException(e);
        }
    }

    /**
     * Create an persistent path
     * @param path path
     * @param obj a object
     * @return the path created
     * @throws Exception
     */
    public String createSequentialJson(String path, Object obj) {
        try {
            return createSequential(path, JSON.toJSONString(obj).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
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
            throw new ZkException(e);
        }
    }

    /**
     * Create a node if not exists
     * @param path path
     * @return return true if create
     * @throws Exception
     */
    public Boolean createIfNotExists(String path) {
        return createIfNotExists(path, (byte[])null);
    }

    /**
     * Create a node if not exists
     * @param path path
     * @param data path data
     * @return return true if create
     * @throws Exception
     */
    public Boolean createIfNotExists(String path, byte[] data) {
        try {
            Stat pathStat = client.checkExists().forPath(path);
            if (pathStat == null){
                String nodePath = client.create().forPath(path, data);
                return Strings.isNullOrEmpty(nodePath) ? Boolean.FALSE : Boolean.TRUE;
            }
        } catch (Exception e) {
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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
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
            throw new RuntimeException("failed to get path data");
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
            return client.getChildren().forPath(path);
        } catch (Exception e) {
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
}