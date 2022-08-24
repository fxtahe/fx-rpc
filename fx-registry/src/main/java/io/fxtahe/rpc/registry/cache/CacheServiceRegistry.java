package io.fxtahe.rpc.registry.cache;

import com.google.common.collect.Lists;
import io.fxtahe.rpc.registry.ServiceRegistry;
import io.fxtahe.rpc.registry.ServiceInstance;
import io.fxtahe.rpc.registry.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * cache local Registration information
 * @author fxtahe
 * @since 2022/8/23 10:25
 */
public class CacheServiceRegistry implements ServiceRegistry{

    private final Map<String, List<ServiceInstance>> registerInstances = new ConcurrentHashMap<>();

    private final Map<String,List<Subscriber>> subscribers = new ConcurrentHashMap<>();

    private final Map<String,List<ServiceInstance>> remoteInstances = new ConcurrentHashMap<>();

    private ServiceRegistry registry;

    public CacheServiceRegistry(ServiceRegistry registry) {
        this.registry = registry;
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        return remoteInstances.get(serviceId);
    }

    @Override
    public List<String> getInstances() {
        return Lists.newArrayList(registerInstances.keySet());
    }

    @Override
    public void register(ServiceInstance registration) {
        List<ServiceInstance> serviceInstances = this.registerInstances.computeIfAbsent(registration.getServiceId(), key -> new ArrayList<>());
        serviceInstances.add(registration);
    }

    @Override
    public void unregister(ServiceInstance registration) {
        List<ServiceInstance> serviceInstances = this.registerInstances.get(registration.getServiceId());
        if(serviceInstances!=null && serviceInstances.size()>0){
            serviceInstances.remove(registration);
            if(serviceInstances.size()==0){
                this.registerInstances.remove(registration.getServiceId());
            }
        }
    }

    @Override
    public List<ServiceInstance> subscribe(Subscriber subscriber) {
        List<Subscriber> subscribers = this.subscribers.computeIfAbsent(subscriber.getServiceId(), key -> new ArrayList<>());
        subscribers.add(subscriber);
        return this.registerInstances.get(subscriber.getServiceId());
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        List<Subscriber> subscribers = this.subscribers.get(subscriber.getServiceId());
        if(subscribers!=null && subscribers.size()>0){
            subscribers.remove(subscriber);
            if(subscribers.size()==0){
                this.subscribers.remove(subscriber.getServiceId());
            }
        }
    }




}
