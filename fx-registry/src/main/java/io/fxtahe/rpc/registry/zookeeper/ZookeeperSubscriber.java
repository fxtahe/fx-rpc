package io.fxtahe.rpc.registry.zookeeper;

import io.fxtahe.rpc.registry.ServiceListener;
import io.fxtahe.rpc.registry.Subscriber;

/**
 * @author fxtahe
 * @since 2022-08-21 22:29
 */
public class ZookeeperSubscriber implements Subscriber {

    private String serviceId;

    private String address;

    private ServiceListener serviceListener;


    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public ServiceListener getServiceListener() {
        return serviceListener;
    }

    @Override
    public String getAddress() {
        return address;
    }
}
