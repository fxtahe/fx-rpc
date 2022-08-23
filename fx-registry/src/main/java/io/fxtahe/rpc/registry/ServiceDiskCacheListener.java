package io.fxtahe.rpc.registry;

import java.util.List;

/**
 * Listen for service changes to refresh the cache
 * @author fxtahe
 * @since 2022-08-23 21:57
 */
public class ServiceDiskCacheListener implements ServiceListener {



    @Override
    public void onStateChange(String serviceId, List<ServiceInstance> serviceInstances, ServiceChangeState newState) {
        //TODO refresh cache
    }
}
