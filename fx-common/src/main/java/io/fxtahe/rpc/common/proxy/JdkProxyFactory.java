package io.fxtahe.rpc.common.proxy;


import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.invoke.Invoker;
import io.fxtahe.rpc.common.util.ClassUtil;

import java.lang.reflect.Proxy;

/**
 * jdk proxy factory
 * @author fxtahe
 * @since 2022/8/19 14:24
 */
@Extension(alias = "jdk")
public class JdkProxyFactory implements ProxyFactory{


    @Override
    public <T> T getProxy(Invoker invoker, Class<?>[] interfaces) {
        return (T) Proxy.newProxyInstance(ClassUtil.getClassLoader(invoker.getClass()),interfaces,new InvokerInvocationHandler(invoker));
    }
}
