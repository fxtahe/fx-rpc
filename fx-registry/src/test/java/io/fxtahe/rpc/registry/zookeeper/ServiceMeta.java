package io.fxtahe.rpc.registry.zookeeper;

import io.fxtahe.rpc.registry.ServiceListener;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author fxtahe
 * @since 2022-08-20 21:59
 */
public class ServiceMeta implements Serializable {

    private String appName;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public static void main(String[] args) {

        Set<ServiceListener> serviceListeners = new HashSet<>();
        ServiceListener serviceListener = (a,b,c)->{};
        serviceListeners.add(serviceListener);
        System.out.println(serviceListeners.size());
        serviceListeners.add((a,b,c)->{});
        System.out.println(serviceListeners.size());



    }
}
