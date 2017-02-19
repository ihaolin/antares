package me.hao0.antares.server.cluster.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Component
public class ServerHost {

    @Value("${server.address:-1}" )
    private String serverHost;

    @Value("${server.port:22222}")
    private Integer serverPort;

    public String get(){
        return serverHost + ":" + serverPort;
    }
}
