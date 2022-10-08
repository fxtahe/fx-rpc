package io.fxtahe.rpc.common.proxy;

import io.fxtahe.rpc.common.invoke.Invoker;

/**
 * @author fxtahe
 * @since 2022/8/19 15:36
 */
public interface ProxyFactory {

     /**
      * get the proxy which wrapper actual invoker in consumer side
      * @param invoker
      * @param interfaces
      * @param <T>
      * @return
      */
     <T> T getProxy(Invoker invoker, Class<T> interfaces);

     /**
      * get the invoker which wrapper actual execute the interface on the provider side
      * @param ref
      * @param interfaceClass
      * @param <T>
      * @return
      */
     <T> Invoker getInvoker(T ref,Class<T> interfaceClass);

}
