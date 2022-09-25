package io.fxtahe.rpc.registry.zookeeper;


import io.fxtahe.rpc.common.config.RegistryConfig;
import io.fxtahe.rpc.common.exception.RegisterException;
import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.registry.RecoverStrategy;
import io.fxtahe.rpc.common.registry.ServiceInstance;
import io.fxtahe.rpc.common.registry.ServiceListener;
import io.fxtahe.rpc.common.registry.ServiceRegistry;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 * @author fxtahe
 * @since 2022-08-20 16:44
 */
@Extension(alias = "zookeeper",singleton = false)
public class ZookeeperServiceRegistry implements ServiceRegistry {


    private ServiceDiscovery<ServiceInstance> serviceDiscovery;

    private ZookeeperListenerRegistry zookeeperListenerRegistry;

    private CuratorFramework client;

    private final String basePath ="/fx-rpc";


    public ZookeeperServiceRegistry(RegistryConfig registryConfig) {
        client= CuratorFrameworkFactory.builder().
                connectString(registryConfig.getConnectionString())
                .connectionTimeoutMs(registryConfig.getConnectTimeout())
                .sessionTimeoutMs(registryConfig.getReadTimeout())
                .retryPolicy(new ExponentialBackoffRetry(1000, 3, 200))
                .build();
        client.start();
        try {
            if(!client.blockUntilConnected(2000, TimeUnit.MILLISECONDS)){
                throw new RegisterException("zookeeper client start fail.");
            }
        } catch (InterruptedException exception) {
            throw new RegisterException("zookeeper client start fail.");
        }

        if(client.getState()!=CuratorFrameworkState.STARTED){
            throw new IllegalStateException("zookeeper client state illegal ");
        }

        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceInstance.class)
                .client(client)
                .basePath(basePath)
                .build();
        this.zookeeperListenerRegistry = new ZookeeperListenerRegistry(serviceDiscovery);
    }

    @Override
    public void register(ServiceInstance registration) {
        try {
            serviceDiscovery.registerService(createInstance(registration));
        } catch (Exception e) {
            throw new RegisterException(registration.getServiceId() + " register service fail.");
        }
    }

    @Override
    public void unregister(ServiceInstance registration) {
        try {
            serviceDiscovery.unregisterService(createInstance(registration));
        } catch (Exception e) {
            throw new RegisterException(registration.getServiceId() + " register service fail.",e);
        }
    }

    @Override
    public void subscribe(String serviceId,ServiceListener serviceListener) {
        zookeeperListenerRegistry.registerServiceListener(serviceId,serviceListener);
    }

    @Override
    public void unsubscribe(String serviceId,ServiceListener serviceListener) {
        zookeeperListenerRegistry.unregisterServiceLister(serviceId,serviceListener);
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        List<ServiceInstance> serviceList;
        try {
            Collection<org.apache.curator.x.discovery.ServiceInstance<ServiceInstance>> serviceInstances = serviceDiscovery.queryForInstances(serviceId);
            serviceList = serviceInstances.stream().map(org.apache.curator.x.discovery.ServiceInstance::getPayload).collect(Collectors.toList());
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


    public org.apache.curator.x.discovery.ServiceInstance createInstance(ServiceInstance registration) throws Exception {
        return org.apache.curator.x.discovery.ServiceInstance.builder().name(registration.getServiceId())
                .id(registration.getId()).address(registration.getHost()).
                        port(registration.getPort()).payload(registration).build();
    }

    public void setRecoverStrategy(RecoverStrategy recoverStrategy){
        client.getConnectionStateListenable().addListener(((curatorFramework, connectionState) -> {
            if(connectionState == ConnectionState.RECONNECTED){
                recoverStrategy.recover();
            }
        }));
    }
}
