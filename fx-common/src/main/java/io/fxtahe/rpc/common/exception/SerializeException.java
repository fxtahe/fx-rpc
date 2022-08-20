package io.fxtahe.rpc.common.exception;

/**
 * 序列化异常
 * @author fxtahe
 * @since 2022/8/18 16:11
 */
public class SerializeException extends RuntimeException{


    public SerializeException() {
        super();
    }

    public SerializeException(String message) {
        super(message);
    }

    public SerializeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializeException(Throwable cause) {
        super(cause);
    }
}
