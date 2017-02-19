package me.hao0.antares.server.cluster.client;

import com.google.common.collect.Maps;
import me.hao0.antares.common.support.Lifecycle;
import me.hao0.antares.common.support.Component;
import me.hao0.antares.common.util.CollectionUtil;
import me.hao0.antares.server.schedule.JobPool;
import me.hao0.antares.store.support.AntaresZkClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@org.springframework.stereotype.Component
public class ClientCluster extends Component implements Lifecycle, InitializingBean, DisposableBean {

    @Autowired
    private JobPool jobPool;

    @Autowired
    private AntaresZkClient zk;

    /**
     * The apps's clients cluster: <appName, AppClientCluster>
     */
    private final Map<String, AppClientCluster> appsClients = Maps.newConcurrentMap();

    private final ConcurrentHashMap<String, AppClientChangedListener> listeners = new ConcurrentHashMap<>();

    public void addListener(String id, AppClientChangedListener listener) {
        this.listeners.putIfAbsent(id, listener);
    }

    public void removeListener(String id){
        this.listeners.remove(id);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    @Override
    public void destroy() throws Exception {
        shutdown();
    }

    @Override
    public void doStart() {

        // init client cluster
        List<String> apps = jobPool.getApps();
        if (!CollectionUtil.isNullOrEmpty(apps)){
            for (String app : apps){
                createAppClientCluster(app);
            }
        }
    }

    @Override
    public void doShutdown() {
        if (!appsClients.isEmpty()){
            for (AppClientCluster clientCluster : appsClients.values()){
                clientCluster.shutdown();
            }
        }
    }

    protected AppClientCluster createAppClientCluster(String appName) {
        AppClientCluster exist = appsClients.get(appName);
        if (exist == null){
            exist = new AppClientCluster(zk, appName) {
                @Override
                public void onClientChanged(String appName, String client, Boolean join) {
                   if (!listeners.isEmpty()){
                       for (AppClientChangedListener listener : listeners.values()){
                           listener.onChanged(appName, client, join);
                       }
                   }
                }
            };
            exist.start();
            appsClients.put(appName, exist);
        }
        return exist;
    }

    /**
     * Whether the app has alive clients or not
     * @param appName the app name
     * @return return true if has clients, or false
     */
    public Boolean hasAliveClients(String appName){
        return !CollectionUtil.isNullOrEmpty(getAliveClients(appName));
    }

    /**
     * Get the alive clients of the app
     * @param appName the app name
     * @return the alive clients of the app
     */
    public List<String> getAliveClients(String appName){

        AppClientCluster appClientCluster = appsClients.get(appName);
        if (appClientCluster == null){
            appClientCluster = createAppClientCluster(appName);
        }

        return appClientCluster.alives();
    }
}
