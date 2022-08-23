package io.fxtahe.rpc.registry.zookeeper;

import com.google.common.collect.Lists;
import io.fxtahe.rpc.common.lifecycle.Closeable;
import io.fxtahe.rpc.registry.RegisterException;
import io.fxtahe.rpc.registry.ServiceListener;
import io.fxtahe.rpc.registry.ServiceChangeState;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.ServiceCacheListener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.ThreadContext.isEmpty;

/**
 * @author fxtahe
 * @since 2022/8/23 13:55
 */
public class ZookeeperListenerRegistry implements Closeable{

    /**
     * key:serviceId
     * value:zookeeper service cache listener
     */
    private final Map<String,ZookeeperListener> cache = new ConcurrentHashMap<>();

    private ServiceDiscovery<ZookeeperServiceInstance> serviceDiscovery;

    public ZookeeperListenerRegistry(ServiceDiscovery<ZookeeperServiceInstance> serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public void registerServiceListener(String serviceId, ServiceListener serviceListener){
        ZookeeperListener zookeeperListener = cache.computeIfAbsent(serviceId,
                key->new ZookeeperListener(Lists.newArrayList(serviceListener),serviceDiscovery.serviceCacheBuilder().name(key).build(),key));
        zookeeperListener.addListener(serviceListener);
    }

    public void unregisterServiceLister(String serviceId,ServiceListener serviceListener){
        ZookeeperListener remove = cache.get(serviceId);
        if(remove!=null){
            remove.removeListener(serviceListener);
            if(remove.listeners.size()==0){
                cache.remove(serviceId);
            }
        }
    }

    @Override
    public void shutdown() throws Exception {
        for(ZookeeperListener zookeeperListener:cache.values()){
            zookeeperListener.shutdown();
        }
    }


    public static class ZookeeperListener implements ServiceCacheListener, Closeable {

        private List<ServiceListener> listeners;

        private ServiceCache<ZookeeperServiceInstance> serviceCache;

        private String serviceId;


        public ZookeeperListener(List<ServiceListener> listeners, ServiceCache<ZookeeperServiceInstance> serviceCache, String serviceId) {
            this.listeners = listeners;
            this.serviceCache = serviceCache;
            this.serviceId = serviceId;
            try {
                serviceCache.start();
            } catch (Exception e) {
                throw new RegisterException("register "+serviceId+" listener fail");
            }
        }

        @Override
        public void cacheChanged() {
            List<ServiceInstance<ZookeeperServiceInstance>> zookeeperServiceInstances = this.serviceCache.getInstances();
            List<io.fxtahe.rpc.registry.ServiceInstance> instances = zookeeperServiceInstances.stream().map(ServiceInstance::getPayload).collect(Collectors.toList());
            ServiceChangeState state = zookeeperServiceInstances.isEmpty()
                    ? ServiceChangeState.DISCONNECTED : ServiceChangeState.CONNECTED;
            for (ServiceListener serviceListener:listeners){
                serviceListener.onStateChange(serviceId,instances,state);
            }
        }

        @Override
        public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
            //
        }

        public void addListener(ServiceListener serviceListener){
            this.listeners.add(serviceListener);
        }


        public void removeListener(ServiceListener serviceListener){
            this.listeners.remove(serviceListener);
        }

        @Override
        public void shutdown() throws Exception {
            serviceCache.close();
        }
    }
}
