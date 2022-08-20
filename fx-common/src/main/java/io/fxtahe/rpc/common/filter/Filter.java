package io.fxtahe.rpc.common.filter;


import io.fxtahe.rpc.common.core.Invocation;

/**
 * @author fxtahe
 * @since 2022/8/18 16:58
 */
public interface Filter {


    /**
     * 请求过滤
     * @param invocation
     */
    void filter(Invocation invocation);


}
