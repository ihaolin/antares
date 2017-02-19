package me.hao0.antares.client.exception;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class AuthFailException extends RuntimeException {

    public AuthFailException() {
    }

    public AuthFailException(String message) {
        super(message);
    }

    public AuthFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthFailException(Throwable cause) {
        super(cause);
    }

    public AuthFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
