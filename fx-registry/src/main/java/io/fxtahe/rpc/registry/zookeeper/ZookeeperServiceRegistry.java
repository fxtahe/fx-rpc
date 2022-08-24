package io.fxtahe.rpc.registry.zookeeper;


import io.fxtahe.rpc.registry.RegisterException;
import io.fxtahe.rpc.registry.ServiceRegistry;
import io.fxtahe.rpc.registry.ServiceListener;
import io.fxtahe.rpc.registry.Subscriber;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author fxtahe
 * @since 2022-08-20 16:44
 */
public class ZookeeperServiceRegistry implements ServiceRegistry {


    private ServiceDiscovery<io.fxtahe.rpc.registry.ServiceInstance> serviceDiscovery;

    private ZookeeperListenerRegistry zookeeperListenerRegistry;

    private final String basePath ="/fx-rpc";

    public ZookeeperServiceRegistry(CuratorFramework curatorFramework) {
        if(curatorFramework.getState()!=CuratorFrameworkState.STARTED){
            throw new IllegalStateException("zookeeper client state illegal ");
        }
        curatorFramework.getConnectionStateListenable().addListener((client, newState) -> {
            if (newState == ConnectionState.RECONNECTED) {
                //TODO recover data . such as  register service, subscribe
            }
        });
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(io.fxtahe.rpc.registry.ServiceInstance.class)
                .client(curatorFramework)
                .basePath(basePath)
                .build();
        this.zookeeperListenerRegistry = new ZookeeperListenerRegistry(serviceDiscovery);
    }

    @Override
    public void register(io.fxtahe.rpc.registry.ServiceInstance registration) {
        try {
            serviceDiscovery.registerService(createInstance(registration));
        } catch (Exception e) {
            throw new RegisterException(registration.getServiceId() + " register service fail.");
        }
    }

    @Override
    public void unregister(io.fxtahe.rpc.registry.ServiceInstance registration) {
        try {
            serviceDiscovery.unregisterService(createInstance(registration));
        } catch (Exception e) {
            throw new RegisterException(registration.getServiceId() + " register service fail.",e);
        }
    }

    @Override
    public List<io.fxtahe.rpc.registry.ServiceInstance> subscribe(Subscriber subscriber) {
        String serviceId = subscriber.getServiceId();
        ServiceListener serviceListener = subscriber.getServiceListener();
        zookeeperListenerRegistry.registerServiceListener(serviceId,serviceListener);
        return getInstances(serviceId);
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        String serviceId = subscriber.getServiceId();
        ServiceListener serviceListener = subscriber.getServiceListener();
        zookeeperListenerRegistry.unregisterServiceLister(serviceId,serviceListener);
    }

    @Override
    public List<io.fxtahe.rpc.registry.ServiceInstance> getInstances(String serviceId) {
        List<io.fxtahe.rpc.registry.ServiceInstance> serviceList;
        try {
            Collection<ServiceInstance<io.fxtahe.rpc.registry.ServiceInstance>> serviceInstances = serviceDiscovery.queryForInstances(serviceId);
            serviceList = serviceInstances.stream().map(ServiceInstance::getPayload).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RegisterException("get service of "+serviceId+" instance fail.");
        }
        return serviceList;
    }

    @Override
    public List<String> getInstances() {
        try {
            return (List<String>) serviceDiscovery.queryForNames();
        } catch (Exception e) {
            throw new RegisterException("get all service id fail.");
        }
    }


    public ServiceInstance createInstance(io.fxtahe.rpc.registry.ServiceInstance registration) throws Exception {
        return ServiceInstance.builder().name(registration.getServiceId())
                .id(registration.getId()).address(registration.getHost()).
                        port(registration.getPort()).payload(registration).build();
    }
}
