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
public class JdkProxyFactory implements ProxyFactory {


    @Override
    public <T> T getProxy(Invoker invoker, Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(ClassUtil.getClassLoader(invoker.getClass()), new Class[]{interfaceClass}, new InvokerInvocationHandler(invoker));
    }

    @Override
    public <T> Invoker getInvoker(T ref, Class<T> interfaceClass) {
        return new Invoker() {
            @Override
            public Result invoke(Invocation invocation) {
                AppResult appResult = new AppResult();
                Object[] arguments = invocation.getArguments();
                String methodName = invocation.getMethodName();
                Class<?>[] parameterTypes = invocation.getParameterTypes();
                try {
                    Method method = ref.getClass().getMethod(methodName, parameterTypes);
                    Object invoke = method.invoke(ref, arguments);
                    appResult.setValue(invoke);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    appResult.setException(e);
                }
                return appResult;
            }

            @Override
            public String getInterfaceName() {
                return interfaceClass.getName();
            }
        };
    }
}
