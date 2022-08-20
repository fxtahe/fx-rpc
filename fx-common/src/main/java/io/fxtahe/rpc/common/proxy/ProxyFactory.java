package io.fxtahe.rpc.common.proxy;

import io.fxtahe.rpc.common.invoke.Invoker;

/**
 * @author fxtahe
 * @since 2022/8/19 15:36
 */
public interface ProxyFactory {


     <T> T getProxy(Invoker invoker, Class<?>[] interfaces);


}
