package me.hao0.antares.tower;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "me.hao0.antares.store",
    "me.hao0.antares.tower",
    "me.hao0.antares.alarm"
})
public class Bootstrap {

    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }
}
