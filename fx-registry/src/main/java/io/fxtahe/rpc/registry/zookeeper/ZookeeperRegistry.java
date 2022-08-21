package io.fxtahe.rpc.registry.zookeeper;


import io.fxtahe.rpc.registry.RegisterException;
import io.fxtahe.rpc.registry.Registry;
import io.fxtahe.rpc.registry.ServiceListener;
import io.fxtahe.rpc.registry.Subscriber;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 *
 * @author fxtahe
 * @since 2022-08-20 16:44
 */
public class ZookeeperRegistry implements Registry<ZookeeperServiceInstance, ZookeeperSubscriber> {


    private ServiceDiscovery<ZookeeperServiceInstance> serviceDiscovery;

    private Map<String, ZookeeperListenerRegistry> cache = new ConcurrentHashMap<>();

    @Override
    public void register(ZookeeperServiceInstance registration) {
        try {
            serviceDiscovery.registerService(createInstance(registration));
            ServiceCache<ZookeeperServiceInstance> serviceCache = serviceDiscovery.serviceCacheBuilder().name(registration.getServiceId()).build();
            ZookeeperListenerRegistry zookeeperListenerRegistry = new ZookeeperListenerRegistry(registration.getServiceId(), new ArrayList<>(), serviceCache);
            cache.putIfAbsent(registration.getServiceId(),zookeeperListenerRegistry);
            serviceCache.start();
        } catch (Exception e) {
            throw new RegisterException(registration.getServiceId() + " register service fail.");
        }
    }

    @Override
    public void unregister(ZookeeperServiceInstance registration) {
        try {
            serviceDiscovery.unregisterService(createInstance(registration));
            Collection<ServiceInstance<ZookeeperServiceInstance>> serviceInstances = serviceDiscovery.queryForInstances(registration.getServiceId());
            if (serviceInstances!=null && serviceInstances.size()==0){
                ZookeeperListenerRegistry listenerRegistry = cache.remove(registration.getServiceId());
                if(listenerRegistry!=null){
                    listenerRegistry.close();
                }
            }
        } catch (Exception e) {
            throw new RegisterException(registration.getServiceId() + " register service fail.",e);
        }
    }

    @Override
    public List<ZookeeperServiceInstance> subscribe(ZookeeperSubscriber subscriber) {
        String serviceId = subscriber.getServiceId();
        ServiceListener serviceListener = subscriber.getServiceListener();
        List<ZookeeperServiceInstance> serviceList;
        try {
            Collection<ServiceInstance<ZookeeperServiceInstance>> serviceInstances = serviceDiscovery.queryForInstances(serviceId);
            serviceList = serviceInstances.stream().map(ServiceInstance::getPayload).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RegisterException("subscribe service "+serviceId+"+fail.");
        }
        ZookeeperListenerRegistry listenerRegistry = cache.get(serviceId);
        if(listenerRegistry!=null){
            listenerRegistry.addListener(serviceListener);
        }
        return serviceList;
    }

    @Override
    public void unsubscribe(ZookeeperSubscriber subscriber) {
        String serviceId = subscriber.getServiceId();
        ServiceListener serviceListener = subscriber.getServiceListener();
        List<ZookeeperServiceInstance> serviceList;
        try {
            Collection<ServiceInstance<ZookeeperServiceInstance>> serviceInstances = serviceDiscovery.queryForInstances(serviceId);
            serviceList = serviceInstances.stream().map(ServiceInstance::getPayload).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RegisterException("subscribe service "+serviceId+"+fail.");
        }
        ZookeeperListenerRegistry listenerRegistry = cache.get(serviceId);

        if(listenerRegistry!=null){
            listenerRegistry.addListener(serviceListener);
        }
        return serviceList;
    }




    public ServiceInstance createInstance(ZookeeperServiceInstance registration) throws Exception {
        return ServiceInstance.builder().name(registration.getServiceId())
                .id(registration.getId()).address(registration.getHost()).
                        port(registration.getPort()).payload(registration).build();
    }
}
