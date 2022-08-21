package io.fxtahe.rpc.registry;

import java.util.List;

/**
 * @author fxtahe
 * @since 2022-08-20 16:30
 */
public interface Registry<S extends ServiceInstance,C extends Subscriber> {

    void register(S registration);

    /**
     * unregister service
     * @param registration service registration
     */
    void unregister(S registration);

    /**
     * subscribe service
     * @return service instance collection
     */
    List<S> subscribe(C subscriber);

    /**
     * unsubscribe service
     */
    void unsubscribe(C subscriber);

}
