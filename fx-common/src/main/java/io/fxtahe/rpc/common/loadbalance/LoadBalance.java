package io.fxtahe.rpc.common.loadbalance;

import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.registry.ServiceInstance;

import java.util.List;

/**
 * @author fxtahe
 * @since 2022/8/31 15:23
 */
public interface LoadBalance {


    List<ServiceInstance> select(Invocation invocation, List<ServiceInstance> serviceInstances);
}
