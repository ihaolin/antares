package me.hao0.antares.server.exception;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class JobInstanceCreateException extends RuntimeException {

    public JobInstanceCreateException() {
        super();
    }

    public JobInstanceCreateException(String message) {
        super(message);
    }

    public JobInstanceCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobInstanceCreateException(Throwable cause) {
        super(cause);
    }

    protected JobInstanceCreateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
