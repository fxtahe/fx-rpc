package io.fxtahe.rpc.common.proxy;

import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.invoke.Invoker;
import io.fxtahe.rpc.common.util.ClassUtil;


/**
 * @author fxtahe
 * @since 2022/10/8 10:36
 */
@Extension(alias = "javassist")
public class JavassistProxyFactory extends AbstractProxyFactory{


    @Override
    public <T> T getProxy(Invoker invoker, Class<T> interfaceClass) {
        return (T) JavassistProxy.newProxyInstance(ClassUtil.getClassLoader(invoker.getClass()), new Class[]{interfaceClass}, new InvokerInvocationHandler(invoker));
    }

}
