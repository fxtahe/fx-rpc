package io.fxtahe.rpc.common.core;

/**
 * @author fxtahe
 * @since 2022/8/19 14:22
 */
public interface Result {

    Object getValue();

    void setValue(Object obj);

    Throwable getException();

    void setException(Throwable t);



}
