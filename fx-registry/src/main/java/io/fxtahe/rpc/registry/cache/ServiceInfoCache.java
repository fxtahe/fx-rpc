package io.fxtahe.rpc.registry.cache;

import io.fxtahe.rpc.common.lifecycle.Closeable;
import io.fxtahe.rpc.common.registry.ServiceInstance;

import java.util.List;

/**
 * cache service interface such as local,redis ...
 * @author fxtahe
 * @since 2022/8/24 14:07
 */
public interface ServiceInfoCache extends Closeable {



    void refreshInstances(String serviceId, List<ServiceInstance> instances);


    List<ServiceInstance> getInstances(String serviceId);



}
