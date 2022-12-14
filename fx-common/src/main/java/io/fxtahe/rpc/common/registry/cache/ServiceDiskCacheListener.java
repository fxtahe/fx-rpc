package io.fxtahe.rpc.common.registry.cache;

import io.fxtahe.rpc.common.registry.ServiceChangeState;
import io.fxtahe.rpc.common.registry.ServiceInstance;
import io.fxtahe.rpc.common.registry.ServiceListener;

import java.util.List;

/**
 * Listen for service changes to refresh the cache
 * @author fxtahe
 * @since 2022-08-23 21:57
 */
public class ServiceDiskCacheListener implements ServiceListener {

    private ServiceInfoCache serviceInfoCache;

    public ServiceDiskCacheListener(ServiceInfoCache serviceInfoCache) {
        this.serviceInfoCache = serviceInfoCache;
    }

    @Override
    public void onStateChange(String serviceId, List<ServiceInstance> serviceInstances, ServiceChangeState newState) {
        serviceInfoCache.refreshInstances(serviceId,serviceInstances);
    }
}
