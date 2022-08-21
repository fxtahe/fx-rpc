package io.fxtahe.rpc.registry.zookeeper;

import io.fxtahe.rpc.registry.ServiceInstance;

import java.util.Map;

/**
 * @author fxtahe
 * @since 2022-08-21 0:20
 */
public class ZookeeperServiceInstance implements ServiceInstance {

    private String id;

    private String serviceId;

    private String host;

    private int port;

    private Map<String,String> metaData;


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public Map<String, String> getMetadata() {
        return metaData;
    }
}
