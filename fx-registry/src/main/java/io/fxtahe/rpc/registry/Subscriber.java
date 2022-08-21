package io.fxtahe.rpc.registry;

/**
 * @author fxtahe
 * @since 2022-08-21 22:22
 */
public interface Subscriber {

    String getServiceId();

    ServiceListener getServiceListener();

    String getAddress();

}
