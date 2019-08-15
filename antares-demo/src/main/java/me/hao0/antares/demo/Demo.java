package me.hao0.antares.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@SpringBootApplication
@ImportResource(locations = {"classpath:antares-context.xml"})
public class Demo {

    public static void main(String[] args) {

        SpringApplication.run(Demo.class, args);

    }
}
