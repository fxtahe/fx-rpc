package io.fxtahe.rpc.common.registry;

import java.util.List;

/**
 * @author fxtahe
 * @since 2022-08-20 16:30
 */
public interface ServiceRegistry{


    RegistryState getState();


    /**
     * get ServiceInstance collection by serviceId
     * @param serviceId service identifier
     * @return collection of ServiceInstance
     */
    List<ServiceInstance> getInstances(String serviceId);

    /**
     * get all serviceId
     * @return collection of serviceId
     */
    List<String> getInstances();

    /**
     * register service
     * @param registration
     */
    void register(ServiceInstance registration);

    /**
     * unregister service
     * @param registration service registration
     */
    void unregister(ServiceInstance registration);

    /**
     * subscribe service
     */
    void subscribe(String serviceId,ServiceListener serviceListener);

    /**
     * unsubscribe service
     */
    void unsubscribe(String serviceId,ServiceListener serviceListener);

    default void setRecoverStrategy(RecoverStrategy recoverStrategy){}

}
