package io.fxtahe.rpc.common.exception;

/**
 * @author fxtahe
 * @since 2022-09-18 21:26
 */
public class RemotingException extends RuntimeException{


    public RemotingException() {
        super();
    }

    public RemotingException(String message) {
        super(message);
    }

    public RemotingException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemotingException(Throwable cause) {
        super(cause);
    }

}
