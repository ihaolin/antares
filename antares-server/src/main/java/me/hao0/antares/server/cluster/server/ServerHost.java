package me.hao0.antares.server.cluster.server;

import me.hao0.antares.common.util.Networks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Component
public class ServerHost {

    @Value("${server.address:-1}")
    private String serverHost;

    @Value("${server.port:22222}")
    private Integer serverPort;

    @PostConstruct
    public void init(){
        if ("-1".equals(serverHost)){
            serverHost = Networks.getSiteIp();
        }
    }

    public String get(){
        return serverHost + ":" + serverPort;
    }
}
