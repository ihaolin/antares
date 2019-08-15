package me.hao0.antares.server;

import me.hao0.antares.server.cluster.server.ServerCluster;
import me.hao0.antares.server.cluster.server.ServerRegister;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "me.hao0.antares.store",
    "me.hao0.antares.server"
})
public class AntaresServer {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(AntaresServer.class, args);

        // start the server register
        ServerRegister serverRegister = context.getBean(ServerRegister.class);
        serverRegister.start();

        // start the server cluster
        ServerCluster serverCluster = context.getBean(ServerCluster.class);
        serverCluster.start();

    }
}
