package io.fxtahe.rpc.common.filter;


import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.core.Result;
import io.fxtahe.rpc.common.invoke.Invoker;

/**
 * @author fxtahe
 * @since 2022/8/18 16:58
 */
public interface Filter {

    /**
     * 请求过滤
     */
    Result filter(Invoker invoker, Invocation invocation);

    /**
     * 异步返回处理
     */
    default void onResponse(Result result, Invocation invocation){

    }

    /**
     * 异步异常返回处理
     */
    default void onError(Throwable throwable, Invocation invocation){

    }

}
