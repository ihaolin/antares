package me.hao0.antares.common.exception;

/**
 * @author haolin
 * @mailto haolin.h0@gmail.com
 */
public class JobFindException extends RuntimeException {
    public JobFindException() {
        super();
    }

    public JobFindException(String message) {
        super(message);
    }

    public JobFindException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobFindException(Throwable cause) {
        super(cause);
    }

    protected JobFindException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
