package io.fxtahe.rpc.registry.cache;

import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.registry.ServiceInstance;
import io.fxtahe.rpc.common.registry.ServiceListener;
import io.fxtahe.rpc.common.registry.ServiceRegistry;
import io.fxtahe.rpc.common.registry.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * cache local Registration information
 *
 * @author fxtahe
 * @since 2022/8/23 10:25
 */
@Extension(alias = "cache",singleton = false)
public class CacheServiceRegistry implements ServiceRegistry {

    private static final Logger log = LoggerFactory.getLogger(CacheServiceRegistry.class);

    private final Map<String, List<ServiceInstance>> registerInstances = new ConcurrentHashMap<>();

    private final Map<String, List<Subscriber>> subscribers = new ConcurrentHashMap<>();

    private ServiceRegistry registry;

    private ServiceListener cacheServiceListener;

    private ServiceInfoCache serviceInfoCache;

    public CacheServiceRegistry(ServiceRegistry registry) {
        this.registry = registry;
        // TODO config cache type
        this.serviceInfoCache = new SimpleServiceInfoCache();
        this.cacheServiceListener = new ServiceDiskCacheListener(serviceInfoCache);
    }


    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        // already subscribe service load from cache
        if (subscribers.containsKey(serviceId)) {
            try {
                List<ServiceInstance> instances = serviceInfoCache.getInstances(serviceId);
                if (instances == null || instances.size() == 0) {
                    instances = registry.getInstances(serviceId);
                    if (instances != null && instances.size() > 0) {
                        serviceInfoCache.refreshInstances(serviceId, instances);
                    }
                }
                return instances;
            } catch (Exception e) {
                return serviceInfoCache.getInstances(serviceId);
            }
        } else {
            return registry.getInstances(serviceId);
        }
    }

    @Override
    public List<String> getInstances() {
        return registry.getInstances();
    }

    @Override
    public void register(ServiceInstance registration) {
        List<ServiceInstance> serviceInstances = this.registerInstances.computeIfAbsent(registration.getServiceId(), key -> new ArrayList<>());
        serviceInstances.add(registration);
        registry.register(registration);
    }

    @Override
    public void unregister(ServiceInstance registration) {
        registry.unregister(registration);
        List<ServiceInstance> serviceInstances = this.registerInstances.get(registration.getServiceId());
        if (serviceInstances != null && serviceInstances.size() > 0) {
            serviceInstances.remove(registration);
            if (serviceInstances.size() == 0) {
                this.registerInstances.remove(registration.getServiceId());
            }
        }
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        List<Subscriber> subscribers = this.subscribers.computeIfAbsent(subscriber.getServiceId(), key -> new ArrayList<>());
        subscribers.add(subscriber);
        registry.subscribe(subscriber);
        //register service change cache listener
        Subscriber cacheSubscriber = new Subscriber(subscriber.getServiceId(), subscriber.getAddress(), cacheServiceListener);
        registry.subscribe(cacheSubscriber);
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        List<Subscriber> subscribers = this.subscribers.get(subscriber.getServiceId());
        if (subscribers != null && subscribers.size() > 0) {
            subscribers.remove(subscriber);
            if (subscribers.size() == 0) {
                this.subscribers.remove(subscriber.getServiceId());
            }
        }
        registry.unsubscribe(subscriber);
        //unregister service change cache listener
        Subscriber cacheSubscriber = new Subscriber(subscriber.getServiceId(), subscriber.getAddress(), cacheServiceListener);
        registry.unsubscribe(cacheSubscriber);
    }


    public void recoverRegisters(){
        log.info("recover registers");
        for(List<ServiceInstance> serviceInstances:registerInstances.values()){
            serviceInstances.forEach(el->registry.register(el));
        }
    }

    public void recoverSubscriber(){
        log.info("recover subscribers");
        for(List<Subscriber> subscriber:subscribers.values()){
            subscriber.forEach(el->registry.subscribe(el));
        }
    }



}
