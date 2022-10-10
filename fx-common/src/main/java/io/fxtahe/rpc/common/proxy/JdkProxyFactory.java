package io.fxtahe.rpc.common.proxy;


import io.fxtahe.rpc.common.core.AppResult;
import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.core.Result;
import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.invoke.Invoker;
import io.fxtahe.rpc.common.util.ClassUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * jdk proxy factory
 *
 * @author fxtahe
 * @since 2022/8/19 14:24
 */
@Extension(alias = "jdk")
public class JdkProxyFactory extends AbstractProxyFactory {


    @Override
    public <T> T getProxy(Invoker invoker, Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(ClassUtil.getClassLoader(invoker.getClass()), new Class[]{interfaceClass}, new InvokerInvocationHandler(invoker));
    }


}
