package me.hao0.antares.store.support;

import me.hao0.antares.common.zk.ZkClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Component
public class AntaresZkClient implements DisposableBean {

    private final ZkClient client;

    private final String zkServers;

    @Autowired
    public AntaresZkClient(
        @Value("${antares.zk.servers:127.0.0.1:2181}") String zkServers,
        @Value("${antares.zk.namespace:ats}") String zkNamespace){
        this.zkServers = zkServers;
        this.client = ZkClient.newClient(zkServers, zkNamespace);
    }

    public ZkClient client(){
        return client;
    }

    public String zkServers(){
        return zkServers;
    }

    @Override
    public void destroy() throws Exception {
        client.shutdown();
    }
}
