package io.fxtahe.rpc.common.router;

import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.registry.ServiceInstance;

import java.util.List;

/**
 * @author fxtahe
 * @since 2022/10/11 16:49
 */
@Extension(alias = "ip",singleton = false)
public class IpRouter implements Router{


    @Override
    public List<ServiceInstance> route(List<ServiceInstance> serviceInstances, Invocation invocation) {

        return serviceInstances;
    }
}
