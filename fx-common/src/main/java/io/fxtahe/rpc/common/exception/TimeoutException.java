package io.fxtahe.rpc.common.exception;

/**
 * @author fxtahe
 * @since 2022/9/29 16:57
 */
public class TimeoutException extends RuntimeException{

    public TimeoutException() {
        super();
    }

    public TimeoutException(String message) {
        super(message);
    }

    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeoutException(Throwable cause) {
        super(cause);
    }
}
