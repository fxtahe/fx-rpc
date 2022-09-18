package io.fxtahe.rpc.common.router;

import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.registry.ServiceInstance;

import java.util.List;

/**
 * @author fxtahe
 * @since 2022-09-16 21:32
 */
public interface Router {


    /**
     * route service
     * @param serviceInstances
     * @param invocation
     * @return
     */
    List<ServiceInstance> route(List<ServiceInstance> serviceInstances, Invocation invocation);

}
