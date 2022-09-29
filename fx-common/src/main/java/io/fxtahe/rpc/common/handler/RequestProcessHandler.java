package io.fxtahe.rpc.common.handler;

import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.core.Result;

/**
 * @author fxtahe
 * @since 2022/9/29 11:08
 */
public interface RequestProcessHandler {

    /**
     * handle invocation
     * @param invocation request
     */
    Result handleRequest(Invocation invocation);

}
