package io.fxtahe.rpc.common.loadbalance;

import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.registry.ServiceInstance;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fxtahe
 * @since 2022/10/11 11:04
 */
@Extension(alias = "round")
public class RoundLoadBalance implements LoadBalance {

    private static final Map<String,Integer> serviceIndex = new ConcurrentHashMap<>();

    @Override
    public ServiceInstance select(List<ServiceInstance> serviceInstances, Invocation invocation) {
        String serviceId = serviceInstances.get(0).getServiceId();
        int index = serviceIndex.computeIfAbsent(serviceId, key -> 0);
        if(index>=serviceInstances.size()-1){
            index =-1;
        }
        ServiceInstance serviceInstance = serviceInstances.get(++index);
        serviceIndex.put(serviceId,index);
        return serviceInstance;
    }
}
