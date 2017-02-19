package me.hao0.antares.common.util;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class Sleeps {

    private Sleeps(){}

    public static void sleep(int secs){
        try {
            Thread.sleep(secs * 1000L);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
