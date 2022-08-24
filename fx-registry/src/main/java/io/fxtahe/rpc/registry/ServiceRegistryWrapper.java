package io.fxtahe.rpc.registry;


import java.util.List;

/**
 * 对注册数据缓存
 * @author fxtahe
 * @since 2022/8/23 18:01
 */
public class ServiceRegistryWrapper implements ServiceRegistry {


    private ServiceRegistry registry;


    public ServiceRegistryWrapper(ServiceRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void register(ServiceInstance registration) {
        this.registry.register(registration);
    }

    @Override
    public void unregister(ServiceInstance registration) {
        this.registry.unregister(registration);
    }

    @Override
    public List<ServiceInstance> subscribe(Subscriber subscriber) {
        return this.registry.subscribe(subscriber);
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        this.registry.unsubscribe(subscriber);
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        return registry.getInstances(serviceId);
    }

    @Override
    public List<String> getInstances() {
        return registry.getInstances();
    }

}
