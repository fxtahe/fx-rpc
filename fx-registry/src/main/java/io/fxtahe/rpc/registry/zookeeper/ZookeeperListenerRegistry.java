package io.fxtahe.rpc.registry.zookeeper;

import io.fxtahe.rpc.registry.ServiceListener;
import io.fxtahe.rpc.registry.ServiceState;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.details.ServiceCacheListener;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * @author fxtahe
 * @since 2022-08-21 16:38
 */
public class ZookeeperListenerRegistry implements ServiceCacheListener, Closeable{

    private List<ServiceListener> listeners;

    private String serviceId;

    private ServiceCache<ZookeeperServiceInstance> serviceCache;

    public ZookeeperListenerRegistry(String serviceId,List<ServiceListener> listeners,  ServiceCache<ZookeeperServiceInstance> serviceCache) {
        this.listeners = listeners;
        this.serviceId = serviceId;
        this.serviceCache = serviceCache;
    }

    @Override
    public void cacheChanged() {
        ServiceState state = this.serviceCache.getInstances().isEmpty()
                ? ServiceState.DISCONNECTED : ServiceState.CONNECTED;
        for (ServiceListener serviceListener:listeners){
            serviceListener.onStateChange(serviceId,state);
        }
    }

    @Override
    public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
        // do other operation when connection state change
    }


    public void addListener(ServiceListener serviceListener){
        this.listeners.add(serviceListener);
    }


    @Override
    public void close() throws IOException {
        serviceCache.close();
    }
}
