package io.fxtahe.rpc.common.cluster;

import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.invoke.Invoker;
import io.fxtahe.rpc.common.lifecycle.Closeable;
import io.fxtahe.rpc.common.registry.ServiceInstance;
import io.fxtahe.rpc.common.registry.ServiceRegistry;

import java.util.List;

/**
 * @author fxtahe
 * @since 2022/8/31 15:21
 */
public interface Cluster extends Invoker, Closeable {



    List<ServiceInstance> list(List<ServiceRegistry> registries,Invocation invocation);


    List<ServiceInstance> route(List<ServiceInstance> serviceInstances, Invocation invocation);


    ServiceInstance loadBalance(List<ServiceInstance> serviceInstances,Invocation invocation);



}
