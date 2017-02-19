package me.hao0.antares.client.exception;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class Server503Exception extends RuntimeException {

    public Server503Exception() {
    }

    public Server503Exception(String message) {
        super(message);
    }

    public Server503Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public Server503Exception(Throwable cause) {
        super(cause);
    }

    public Server503Exception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
