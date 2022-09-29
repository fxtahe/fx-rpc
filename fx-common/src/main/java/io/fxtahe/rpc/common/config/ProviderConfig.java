package io.fxtahe.rpc.common.config;

import io.fxtahe.rpc.common.bootstrap.BootStrap;
import io.fxtahe.rpc.common.bootstrap.BootStrapFactory;
import io.fxtahe.rpc.common.invoke.Invoker;
import io.fxtahe.rpc.common.invoke.ProviderProxyInvoker;
import io.fxtahe.rpc.common.registry.ServiceInstance;
import io.fxtahe.rpc.common.registry.ServiceRegistry;
import io.fxtahe.rpc.common.registry.ServiceRegistryFactory;
import io.fxtahe.rpc.common.remoting.ThreadPoolRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author fxtahe
 * @since 2022/8/19 15:03
 */
public class ProviderConfig<T> extends AbstractConfig<T, ProviderConfig<T>> {


    private List<ServerConfig> bootStraps;

    private T ref;

    private String reflectType = "jdk";

    private volatile boolean exported;

    private Invoker invoker;

    private ExecutorService executor;

    /**
     * 发布服务
     */
    public void export() {
        if (exported) {
            return;
        }
        synchronized (this) {
            invoker = new ProviderProxyInvoker(ref, interfaceClass, reflectType);
            for (ServerConfig serverConfig : bootStraps) {
                BootStrap bootStrap = BootStrapFactory.buildBootStrap(serverConfig.getServerType());
                bootStrap.export(invoker,serverConfig);
                if(executor!=null){
                    ThreadPoolRegister.registerThreadPool(interfaceClass.getName(),executor);
                }
                if (registries != null) {
                    for (RegistryConfig registryConfig : registries) {
                        ServiceRegistry serviceRegistry = ServiceRegistryFactory.buildRegistry(registryConfig);
                        ServiceInstance serviceInstance = new ServiceInstance();
                        serviceInstance.setServiceId(interfaceClass.getName());
                        serviceInstance.setHost(serverConfig.getHost());
                        serviceInstance.setPort(serverConfig.getPort());
                        serviceInstance.setId(serverConfig.getHost() + ":" + serverConfig.getPort());
                        serviceRegistry.register(serviceInstance);
                    }
                }
            }
            exported = true;
        }
    }


    public void unExport() {
        if (!exported) {
            return;
        }
        synchronized (this) {
            for (ServerConfig serverConfig : bootStraps) {
                BootStrap bootStrap = BootStrapFactory.buildBootStrap(serverConfig.getServerType());
                bootStrap.unExport(invoker);
                if(executor!=null){
                    ThreadPoolRegister.unRegisterThreadPool(interfaceClass.getName());
                }
                if (registries != null) {
                    for (RegistryConfig registryConfig : registries) {
                        ServiceRegistry serviceRegistry = ServiceRegistryFactory.buildRegistry(registryConfig);
                        ServiceInstance serviceInstance = new ServiceInstance();
                        serviceInstance.setServiceId(interfaceClass.getName());
                        serviceInstance.setHost(serverConfig.getHost());
                        serviceInstance.setPort(serverConfig.getPort());
                        serviceInstance.setId(serverConfig.getHost() + ":" + serverConfig.getPort());
                        serviceRegistry.unregister(serviceInstance);
                    }
                }
            }
            exported = false;
        }
    }


    public T getRef() {
        return ref;
    }

    public void setRef(T ref) {
        this.ref = ref;
    }


    public void setBootStrap(ServerConfig server) {
        if (bootStraps == null || bootStraps.isEmpty()) {
            bootStraps = new ArrayList<>();
        }
        bootStraps.add(server);
    }

    public void setBootStrap(List<ServerConfig> servers) {
        this.bootStraps = servers;
    }


    public String getReflectType() {
        return reflectType;
    }

    public void setReflectType(String reflectType) {
        this.reflectType = reflectType;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }
}
