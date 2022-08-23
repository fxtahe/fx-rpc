package io.fxtahe.rpc.common.exception;

import java.text.MessageFormat;

/**
 * 序列化异常
 * @author fxtahe
 * @since 2022/8/18 16:11
 */
public class SerializeException extends RuntimeException{


    private Class<?> targetClass;

    private static final String errorMsgFormat = "Serialize target class {0} fail ,case error{1}";

    private static final String msgFormat = "Serialize target class {0} fail .";

    public SerializeException(Class<?> targetClass) {
        super(MessageFormat.format(msgFormat,targetClass.getName()));
        this.targetClass =targetClass;
    }

    public SerializeException(Class<?> targetClass,String message) {
        super(MessageFormat.format(errorMsgFormat,targetClass.getName(),message));
        this.targetClass = targetClass;
    }

    public SerializeException(Class<?> targetClass,String message, Throwable cause) {
        super(MessageFormat.format(errorMsgFormat, targetClass.getName(), message), cause);
        this.targetClass = targetClass;
    }

    public SerializeException(Class<?> targetClass,Throwable cause) {
        super(MessageFormat.format(errorMsgFormat, targetClass.getName(), cause.getMessage()),cause);
        this.targetClass = targetClass;
    }
}
