package io.fxtahe.rpc.registry;

/**
 * @author fxtahe
 * @since 2022-08-21 20:51
 */
public class RegisterException extends RuntimeException {


    public RegisterException() {
        super();
    }

    public RegisterException(String message) {
        super(message);
    }

    public RegisterException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegisterException(Throwable cause) {
        super(cause);
    }
}
