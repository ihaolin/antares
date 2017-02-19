package me.hao0.antares.store.exception;

/**
 * @author haolin
 * @mailto haolin.h0@gmail.com
 */
public class JobInstanceNotExistException extends RuntimeException {
    public JobInstanceNotExistException() {
        super();
    }

    public JobInstanceNotExistException(String message) {
        super(message);
    }

    public JobInstanceNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobInstanceNotExistException(Throwable cause) {
        super(cause);
    }

    protected JobInstanceNotExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
