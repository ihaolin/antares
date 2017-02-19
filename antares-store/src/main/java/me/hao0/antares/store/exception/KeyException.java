package me.hao0.antares.store.exception;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class KeyException extends RuntimeException {
    public KeyException() {
        super();
    }

    public KeyException(String message) {
        super(message);
    }

    public KeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyException(Throwable cause) {
        super(cause);
    }

    protected KeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
