package io.fxtahe.rpc.registry;

import java.util.List;

/**
 * service discovery
 * @author fxtahe
 * @since 2022-08-23 22:32
 */
public interface ServiceDiscovery<S extends ServiceInstance> {

    /**
     * get ServiceInstance collection by serviceId
     * @param serviceId service identifier
     * @return collection of ServiceInstance
     */
    List<S> getInstances(String serviceId);

    /**
     * get all serviceId
     * @return collection of serviceId
     */
    List<String> getInstances();

}
