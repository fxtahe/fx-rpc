package io.fxtahe.rpc.common.invoke;

import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.core.Result;
import io.fxtahe.rpc.common.ext.ExtensionLoaderFactory;
import io.fxtahe.rpc.common.filter.FilterChainBuilder;
import io.fxtahe.rpc.common.proxy.ProxyFactory;

/**
 * @author fxtahe
 * @since 2022/9/13 17:36
 */
public class ProviderProxyInvoker implements Invoker{


    private final Invoker filterChainInvoker;

    public ProviderProxyInvoker(Object ref, Class interfaceClass, String reflectType) {
        ProxyFactory proxyFactory = ExtensionLoaderFactory.getExtensionLoader(ProxyFactory.class).getInstance(reflectType);
        Invoker originInvoker = proxyFactory.getInvoker(ref, interfaceClass);
        filterChainInvoker = FilterChainBuilder.buildFilterChain(originInvoker, "provider");
    }

    @Override
    public Result invoke(Invocation invocation) {
        return filterChainInvoker.invoke(invocation);
    }

    @Override
    public String getInterfaceName() {
        return filterChainInvoker.getInterfaceName();
    }
}
