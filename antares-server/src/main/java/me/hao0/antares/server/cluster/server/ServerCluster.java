package me.hao0.antares.server.cluster.server;

import com.google.common.collect.Lists;
import me.hao0.antares.common.log.Logs;
import me.hao0.antares.common.support.Lifecycle;
import me.hao0.antares.common.support.Component;
import me.hao0.antares.common.util.ZkPaths;
import me.hao0.antares.common.zk.ChildListener;
import me.hao0.antares.common.zk.ChildWatcher;
import me.hao0.antares.store.support.AntaresZkClient;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;
import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@org.springframework.stereotype.Component
public class ServerCluster extends Component implements Lifecycle {

    @Autowired
    private AntaresZkClient zk;

    /**
     * The watcher monitor servers
     */
    private ChildWatcher watcher;

    /**
     * The alive servers
     */
    private List<String> alives = Lists.newCopyOnWriteArrayList();

    private List<ServerChangedListener> listeners = Lists.newCopyOnWriteArrayList();

    public void addListener(ServerChangedListener listener){
        listeners.add(listener);
    }

    @Override
    public void doStart(){

        // watching the alive servers
        watcher = zk.client().newChildWatcher(ZkPaths.SERVERS, new ChildListener() {

            @Override
            protected void onAdd(String path, byte[] data) {
                String server = ZkPaths.lastNode(path);
                if (!alives.contains(server)){
                    alives.add(server);
                    notifyListeners(server, true);
                    Logs.info("The server ({}) is joined.", server);
                }
            }

            @Override
            protected void onDelete(String path) {

                String server = ZkPaths.lastNode(path);

                if(alives.remove(server)){
                    notifyListeners(server, false);
                    Logs.warn("The server ({}) is left.", server);
                }
            }
        });
    }

    private void notifyListeners(String server, boolean join) {
        if (!listeners.isEmpty()){
            for (ServerChangedListener listener : listeners){
                listener.onServerChanged(server, join);
            }
        }
    }

    public List<String> alives() {
        return alives;
    }

    @PreDestroy
    public void destroy(){
        shutdown();
    }

    @Override
    protected void doShutdown() {
        if (watcher != null){
            watcher.stop();
        }
    }
}
