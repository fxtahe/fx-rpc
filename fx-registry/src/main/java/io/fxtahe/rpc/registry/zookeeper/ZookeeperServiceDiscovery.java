package io.fxtahe.rpc.registry.zookeeper;

import io.fxtahe.rpc.registry.RegisterException;
import io.fxtahe.rpc.registry.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fxtahe
 * @since 2022-08-23 22:44
 */
public class ZookeeperServiceDiscovery implements ServiceDiscovery<ZookeeperServiceInstance> {


    private org.apache.curator.x.discovery.ServiceDiscovery<ZookeeperServiceInstance> serviceDiscovery;


    @Override
    public List<ZookeeperServiceInstance> getInstances(String serviceId) {
        try {
            Collection<ServiceInstance<ZookeeperServiceInstance>> serviceInstances =
                    serviceDiscovery.queryForInstances(serviceId);
            return serviceInstances.stream().map(ServiceInstance::getPayload).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RegisterException("error getting service instances from zookeeper for " + serviceId);
        }
    }

    @Override
    public List<String> getInstances() {
        try {
            Collection<String> serviceIds = serviceDiscovery.queryForNames();
            return (List<String>) serviceIds;
        } catch (Exception e) {
            throw new RegisterException("error getting serviceIds from zookeeper for ");
        }
    }
}
