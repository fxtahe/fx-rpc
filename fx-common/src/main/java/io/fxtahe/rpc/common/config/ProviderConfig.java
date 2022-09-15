package io.fxtahe.rpc.common.config;

import io.fxtahe.rpc.common.bootstrap.BootStrap;
import io.fxtahe.rpc.common.bootstrap.BootStrapFactory;
import io.fxtahe.rpc.common.invoke.Invoker;
import io.fxtahe.rpc.common.invoke.ProviderProxyInvoker;
import io.fxtahe.rpc.common.registry.ServiceInstance;
import io.fxtahe.rpc.common.registry.ServiceRegistry;
import io.fxtahe.rpc.common.registry.ServiceRegistryFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fxtahe
 * @since 2022/8/19 15:03
 */
public class ProviderConfig<T> extends AbstractConfig<T, ProviderConfig<T>> {


    private List<BootStrapConfig> bootStraps;

    private T ref;

    private String reflectType = "jdk";

    private volatile boolean exported;

    private Invoker invoker;

    /**
     * 发布服务
     */
    public void export() {
        if (exported) {
            return;
        }
        synchronized (this) {
            invoker = new ProviderProxyInvoker(ref, interfaceClass, reflectType);
            for (BootStrapConfig bootStrapConfig : bootStraps) {
                BootStrap bootStrap = BootStrapFactory.buildBootStrap(bootStrapConfig);
                bootStrap.export(invoker);
                if (registries != null) {
                    for (RegistryConfig registryConfig : registries) {
                        ServiceRegistry serviceRegistry = ServiceRegistryFactory.buildRegistry(registryConfig);
                        ServiceInstance serviceInstance = new ServiceInstance();
                        serviceInstance.setServiceId(interfaceClass.getName());
                        serviceInstance.setHost(bootStrapConfig.getHost());
                        serviceInstance.setPort(bootStrapConfig.getPort());
                        serviceInstance.setId(bootStrapConfig.getHost() + ":" + bootStrapConfig.getPort());
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
            for (BootStrapConfig bootStrapConfig : bootStraps) {
                BootStrap bootStrap = BootStrapFactory.buildBootStrap(bootStrapConfig);
                bootStrap.unExport(invoker);
                if (registries != null) {
                    for (RegistryConfig registryConfig : registries) {
                        ServiceRegistry serviceRegistry = ServiceRegistryFactory.buildRegistry(registryConfig);
                        ServiceInstance serviceInstance = new ServiceInstance();
                        serviceInstance.setServiceId(interfaceClass.getName());
                        serviceInstance.setHost(bootStrapConfig.getHost());
                        serviceInstance.setPort(bootStrapConfig.getPort());
                        serviceInstance.setId(bootStrapConfig.getHost() + ":" + bootStrapConfig.getPort());
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


    public void setBootStrap(BootStrapConfig server) {
        if (bootStraps == null || bootStraps.isEmpty()) {
            bootStraps = new ArrayList<>();
        }
        bootStraps.add(server);
    }

    public void setBootStrap(List<BootStrapConfig> servers) {
        this.bootStraps = servers;
    }


    public String getReflectType() {
        return reflectType;
    }

    public void setReflectType(String reflectType) {
        this.reflectType = reflectType;
    }
}
