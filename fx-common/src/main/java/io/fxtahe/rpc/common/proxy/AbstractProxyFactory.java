package io.fxtahe.rpc.common.proxy;

import io.fxtahe.rpc.common.core.AppResult;
import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.core.Result;
import io.fxtahe.rpc.common.invoke.Invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author fxtahe
 * @since 2022/10/10 9:41
 */
public abstract class AbstractProxyFactory implements ProxyFactory{


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
