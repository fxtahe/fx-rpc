package io.fxtahe.rpc.common.exception;

/**
 * @author fxtahe
 * @since 2022/8/23 9:59
 */
public class DeSerializeException extends RuntimeException{

    private Class<?> targetClass;



    public DeSerializeException(Class<?> targetClass,String message) {
        super(message);
        this.targetClass = targetClass;
    }

    public DeSerializeException(Class<?> targetClass,String message, Throwable cause) {
        super(message, cause);
        this.targetClass = targetClass;
    }

    public DeSerializeException(Class<?> targetClass,Throwable cause) {
        super(cause);
        this.targetClass = targetClass;
    }
}
