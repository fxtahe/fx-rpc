package io.fxtahe.rpc.registry.zookeeper;

import com.google.common.collect.Sets;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * @author fxtahe
 * @since 2022/8/23 13:55
 */
public class ZookeeperListenerRegistry implements Closeable{

    private static final Logger log = LoggerFactory.getLogger(ZookeeperListenerRegistry.class);

    /**
     * key:serviceId
     * value:zookeeper service cache listener
     */
    private final Map<String,ZookeeperListener> cache = new ConcurrentHashMap<>();

    private ServiceDiscovery<io.fxtahe.rpc.registry.ServiceInstance> serviceDiscovery;

    public ZookeeperListenerRegistry(ServiceDiscovery<io.fxtahe.rpc.registry.ServiceInstance> serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public void registerServiceListener(String serviceId, ServiceListener serviceListener){
        if(serviceListener!=null){
            ZookeeperListener zookeeperListener = cache.computeIfAbsent(serviceId,
                    key->new ZookeeperListener(Sets.newConcurrentHashSet(),serviceDiscovery.serviceCacheBuilder().name(key).build(),key));
            zookeeperListener.addListener(serviceListener);
        }
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

        private Set<ServiceListener> listeners;

        private ServiceCache<io.fxtahe.rpc.registry.ServiceInstance> serviceCache;

        private String serviceId;


        public ZookeeperListener(Set<ServiceListener> listeners, ServiceCache<io.fxtahe.rpc.registry.ServiceInstance> serviceCache, String serviceId) {
            this.listeners = listeners;
            this.serviceCache = serviceCache;
            this.serviceId = serviceId;
            try {
                serviceCache.addListener(this);
                serviceCache.start();
            } catch (Exception e) {
                throw new RegisterException("register "+serviceId+" listener fail");
            }
        }

        @Override
        public void cacheChanged() {
            if(log.isDebugEnabled()){
                log.info("{} zookeeper cache change",serviceId);
            }
            List<ServiceInstance<io.fxtahe.rpc.registry.ServiceInstance>> zookeeperServiceInstances = this.serviceCache.getInstances();
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
