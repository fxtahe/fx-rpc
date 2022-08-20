package io.fxtahe.rpc.common.invoke;

import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.core.Result;

/**
 * @author fxtahe
 * @since 2022/8/19 11:57
 */
public interface Invoker {

    /**
     * @param invocation
     * @return result
     */
    Result invoke(Invocation invocation);

    Class<?> getInterface();
}
