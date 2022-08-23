package io.fxtahe.rpc.registry;

import java.util.List;

/**
 * 对注册数据缓存
 * @author fxtahe
 * @since 2022/8/23 18:01
 */
public class ServiceRegistryCacheDecorator implements ServiceRegistry<ServiceInstance,Subscriber> {

    private ServiceRegistry<ServiceInstance,Subscriber> registry;

    private ServiceDiscovery serviceDiscovery;

    private String cacheDir;





    @Override
    public void register(ServiceInstance registration) {
        //进行cache缓存
        this.registry.register(registration);

    }

    @Override
    public void unregister(ServiceInstance registration) {
        //清理
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
}
