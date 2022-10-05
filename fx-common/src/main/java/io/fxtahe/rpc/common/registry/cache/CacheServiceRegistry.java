package io.fxtahe.rpc.common.registry.cache;

import io.fxtahe.rpc.common.exception.RegisterException;
import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.registry.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * cache local Registration information
 *
 * @author fxtahe
 * @since 2022/8/23 10:25
 */
@Extension(alias = "cache",singleton = false)
public class CacheServiceRegistry implements ServiceRegistry ,ServiceListener{

    private static final Logger log = LoggerFactory.getLogger(CacheServiceRegistry.class);

    private final Map<String, List<ServiceInstance>> registerInstances = new ConcurrentHashMap<>();

    private final Map<String, List<ServiceListener>> subscribers = new ConcurrentHashMap<>();

    private ServiceRegistry registry;

    private ServiceListener cacheServiceListener;

    private ServiceInfoCache serviceInfoCache;

    private String registryType;

    private boolean failOver;

    public CacheServiceRegistry(ServiceRegistry registry,String registryType,String cachePath, boolean failOver){
        this.registry = registry;
        this.registryType = registryType;
        this.failOver = failOver;
        this.registry.setRecoverStrategy(()->{this.recoverRegisters();this.recoverSubscriber();});
        this.serviceInfoCache = new SimpleServiceInfoCache(failOver,registryType,cachePath);
        this.cacheServiceListener = new ServiceDiskCacheListener(serviceInfoCache);
    }


    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        // already subscribe service load from cache
        if (subscribers.containsKey(serviceId) || RegistryState.DISCONNECTED.equals(getState())) {
            try {
                List<ServiceInstance> instances = serviceInfoCache.getInstances(serviceId);
                if (instances == null || instances.size() == 0) {
                    instances = registry.getInstances(serviceId);
                    serviceInfoCache.refreshInstances(serviceId, instances);
                }
                return instances;
            } catch (Exception e) {
                return serviceInfoCache.getInstances(serviceId);
            }
        } else {
            try {
                return registry.getInstances(serviceId);
            }catch (RegisterException e){
                if(failOver){
                    return serviceInfoCache.getInstances(serviceId);
                }
                throw e;
            }
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
        if(RegistryState.CONNECTED.equals(registry.getState())){
            registry.register(registration);
        }
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
    public void subscribe(String serviceId,ServiceListener serviceListener) {
        List<ServiceListener> subscribers = this.subscribers.computeIfAbsent(serviceId, key -> new ArrayList<>());
        if(serviceListener!=null)subscribers.add(serviceListener);
        if(RegistryState.CONNECTED.equals(registry.getState())) {
            registry.subscribe(serviceId, this);
        }
    }

    @Override
    public void unsubscribe(String serviceId,ServiceListener serviceListener) {
        List<ServiceListener> subscribers = this.subscribers.get(serviceId);
        if (subscribers != null && subscribers.size() > 0) {
            subscribers.remove(serviceListener);
            if (subscribers.size() == 0) {
                this.subscribers.remove(serviceId);
            }
        }else{
            registry.unsubscribe(serviceId,this);
        }
    }


    public void recoverRegisters(){
        log.info("recover registers");
        for(List<ServiceInstance> serviceInstances:registerInstances.values()){
            serviceInstances.forEach(el->registry.register(el));
        }
    }

    public void recoverSubscriber(){
        log.info("recover subscribers");
        for(String key: subscribers.keySet()){
            registry.subscribe(key,this);
        }
    }

    @Override
    public void onStateChange(String serviceId, List<ServiceInstance> serviceInstances, ServiceChangeState newState) {
        cacheServiceListener.onStateChange(serviceId,serviceInstances,newState);
        List<ServiceListener> serviceListeners = subscribers.get(serviceId);
        if(serviceInstances!=null){
            serviceListeners.forEach(serviceListener -> {
                serviceListener.onStateChange(serviceId,serviceInstances,newState);
            });
        }
    }

    @Override
    public RegistryState getState() {
        return registry.getState();
    }
}
