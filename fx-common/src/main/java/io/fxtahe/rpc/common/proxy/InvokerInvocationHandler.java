package io.fxtahe.rpc.common.proxy;

import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.invoke.Invoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author fxtahe
 * @since 2022/8/23 13:55
 */
public class InvokerInvocationHandler implements InvocationHandler {


    private Invoker invoker;

    public InvokerInvocationHandler(Invoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(invoker, args);
        }
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) {
            if ("toString".equals(methodName)) {
                return invoker.toString();
            } else if ("hashCode".equals(methodName)) {
                return invoker.hashCode();
            }
        } else if (parameterTypes.length == 1 && "equals".equals(methodName)) {
            return invoker.equals(args[0]);
        }

        Invocation invocation = new Invocation( method.getName(),invoker.getInterface().getName(),args, method.getParameterTypes() );



        return null;
    }
}
