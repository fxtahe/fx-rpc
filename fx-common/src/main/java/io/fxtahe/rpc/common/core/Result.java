package io.fxtahe.rpc.common.core;

import java.io.Serializable;
import java.util.function.BiConsumer;

/**
 * @author fxtahe
 * @since 2022/8/19 14:22
 */
public interface Result extends Serializable {

    Object getValue();

    void setValue(Object obj);


    boolean hasException();

    Throwable getException();

    void setException(Throwable t);

    /**
     * async return implement this function
     */
    default Result whenComplete(BiConsumer<Result, Throwable> fn){
        return this;
    }


    Object recreate() throws Throwable;
}
