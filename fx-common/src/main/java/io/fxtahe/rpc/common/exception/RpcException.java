package io.fxtahe.rpc.common.exception;

/**
 * @author fxtahe
 * @since 2022-09-24 20:51
 */
public class RpcException extends RuntimeException{

    public RpcException() {
        super();
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }
}
