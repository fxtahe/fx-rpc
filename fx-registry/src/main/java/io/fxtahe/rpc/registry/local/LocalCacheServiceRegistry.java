package io.fxtahe.rpc.registry.local;

import io.fxtahe.rpc.registry.ServiceRegistry;
import io.fxtahe.rpc.registry.ServiceChangeState;
import io.fxtahe.rpc.registry.ServiceInstance;
import io.fxtahe.rpc.registry.ServiceListener;
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
public class LocalCacheServiceRegistry implements ServiceRegistry<ServiceInstance,Subscriber> {

    private final Map<String, List<ServiceInstance>> serviceInstances = new ConcurrentHashMap<>();

    private final Map<String,List<Subscriber>> subscribers = new ConcurrentHashMap<>();

    @Override
    public void register(ServiceInstance registration) {
        List<ServiceInstance> serviceInstances = this.serviceInstances.computeIfAbsent(registration.getServiceId(), key -> new ArrayList<>());
        serviceInstances.add(registration);
        notifyChange(registration,ServiceChangeState.CONNECTED);
    }

    @Override
    public void unregister(ServiceInstance registration) {
        List<ServiceInstance> serviceInstances = this.serviceInstances.get(registration.getServiceId());
        if(serviceInstances!=null && serviceInstances.size()>0){
            serviceInstances.remove(registration);
            if(serviceInstances.size()==0){
                this.serviceInstances.remove(registration.getServiceId());
            }
        }
        notifyChange(registration,ServiceChangeState.DISCONNECTED);
    }

    @Override
    public List<ServiceInstance> subscribe(Subscriber subscriber) {
        List<Subscriber> subscribers = this.subscribers.computeIfAbsent(subscriber.getServiceId(), key -> new ArrayList<>());
        subscribers.add(subscriber);
        return this.serviceInstances.get(subscriber.getServiceId());
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


    public void notifyChange(ServiceInstance serviceInstance, ServiceChangeState state){
        String serviceId = serviceInstance.getServiceId();
        for(Subscriber subscriber:subscribers.get(serviceId)){
            ServiceListener serviceListener = subscriber.getServiceListener();
            serviceListener.onStateChange(serviceId,state);
        }
    }
}
