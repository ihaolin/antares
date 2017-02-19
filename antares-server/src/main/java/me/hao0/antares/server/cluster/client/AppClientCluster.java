package me.hao0.antares.server.cluster.client;

import com.google.common.collect.Lists;
import me.hao0.antares.common.log.Logs;
import me.hao0.antares.common.support.Lifecycle;
import me.hao0.antares.common.support.Component;
import me.hao0.antares.common.util.CollectionUtil;
import me.hao0.antares.common.util.ZkPaths;
import me.hao0.antares.common.zk.ChildListener;
import me.hao0.antares.common.zk.ChildWatcher;
import me.hao0.antares.store.support.AntaresZkClient;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The app's clients cluster
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public abstract class AppClientCluster extends Component implements Lifecycle {

    private final String appName;

    private final Set<String> alives = new HashSet<>();

    private final AntaresZkClient zk;

    private ChildWatcher watcher;

    public AppClientCluster(AntaresZkClient zk, String appName){

        this.appName = appName;
        this.zk = zk;

        // get alive clients once
        String appClientsPath = ZkPaths.pathOfAppClients(appName);
        zk.client().mkdirs(appClientsPath);
        List<String> clients = zk.client().gets(appClientsPath);
        if (!CollectionUtil.isNullOrEmpty(clients)){
            alives.addAll(clients);
        }
    }

    /**
     * Get current alive clients of the app
     * @return current alive clients of the app
     */
    public List<String> alives() {
        return Lists.newArrayList(alives);
    }

    @Override
    public void doStart() {
        // listen app's clients change
        watcher = zk.client().newChildWatcher(ZkPaths.pathOfAppClients(appName), new ChildListener() {
            @Override
            protected void onAdd(String path, byte[] data) {

                // not started, or has shutdown
                // prevent multiple redundant notifies before started
                if (!started || shutdowned) {
                    return;
                }

                String client = ZkPaths.lastNode(path);
                if (alives.contains(client)){
                    return;
                }

                alives.add(client);
                onClientChanged(appName, client, true);
                Logs.info("The app({})'s client({}) joined.", appName, client);
            }

            @Override
            protected void onDelete(String path) {
                String client = ZkPaths.lastNode(path);
                alives.remove(client);
                onClientChanged(appName, client, false);
                Logs.info("The app({})'s client({}) left.", appName, client);
            }
        });
    }

    @Override
    public void doShutdown() {
        watcher.stop();
    }

    /**
     * Callback when the app's client changed
     * @param appName the app name
     * @param client the client host
     * @param join true is join, false is left
     */
    public abstract void onClientChanged(String appName, String client, Boolean join);
}
