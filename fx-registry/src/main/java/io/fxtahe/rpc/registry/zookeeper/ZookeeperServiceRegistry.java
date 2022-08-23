package io.fxtahe.rpc.registry.zookeeper;


import io.fxtahe.rpc.registry.RegisterException;
import io.fxtahe.rpc.registry.ServiceRegistry;
import io.fxtahe.rpc.registry.ServiceListener;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author fxtahe
 * @since 2022-08-20 16:44
 */
public class ZookeeperServiceRegistry implements ServiceRegistry<ZookeeperServiceInstance, ZookeeperSubscriber> {


    private ServiceDiscovery<ZookeeperServiceInstance> serviceDiscovery;

    private ZookeeperListenerRegistry zookeeperListenerRegistry;

    public ZookeeperServiceRegistry(ServiceDiscovery<ZookeeperServiceInstance> serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
        this.zookeeperListenerRegistry = new ZookeeperListenerRegistry(serviceDiscovery);
    }

    @Override
    public void register(ZookeeperServiceInstance registration) {
        try {
            serviceDiscovery.registerService(createInstance(registration));
        } catch (Exception e) {
            throw new RegisterException(registration.getServiceId() + " register service fail.");
        }
    }

    @Override
    public void unregister(ZookeeperServiceInstance registration) {
        try {
            serviceDiscovery.unregisterService(createInstance(registration));
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
            zookeeperListenerRegistry.registerServiceListener(serviceId,serviceListener);
        } catch (Exception e) {
            throw new RegisterException("subscribe service "+serviceId+"+fail.");
        }
        return serviceList;
    }

    @Override
    public void unsubscribe(ZookeeperSubscriber subscriber) {
        String serviceId = subscriber.getServiceId();
        ServiceListener serviceListener = subscriber.getServiceListener();
        zookeeperListenerRegistry.unregisterServiceLister(serviceId,serviceListener);
    }



    public ServiceInstance createInstance(ZookeeperServiceInstance registration) throws Exception {
        return ServiceInstance.builder().name(registration.getServiceId())
                .id(registration.getId()).address(registration.getHost()).
                        port(registration.getPort()).payload(registration).build();
    }
}
