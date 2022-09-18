package io.fxtahe.rpc.common.bootstrap;

import io.fxtahe.rpc.common.config.ServerConfig;
import io.fxtahe.rpc.common.invoke.Invoker;
import io.fxtahe.rpc.common.registry.ServiceInstance;
import io.fxtahe.rpc.common.remoting.Client;

/**
 * @author fxtahe
 * @since 2022/9/13 14:43
 */
public interface BootStrap {

    /**
     * export service
     */
    void export(Invoker invoker, ServerConfig serverConfig);

    /**
     * unExport service
     */
    void unExport(Invoker invoker);

    /**
     *
     */
    Client refer(ServiceInstance serviceInstance);

    /**
     *
     */
    void unRefer(String serviceId);

}
