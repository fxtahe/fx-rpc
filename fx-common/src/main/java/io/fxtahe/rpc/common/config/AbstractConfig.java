package io.fxtahe.rpc.common.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fxtahe
 * @since 2022/8/19 15:03
 */
public abstract class AbstractConfig<T,S extends AbstractConfig<T, S>> {


    protected Class<T> interfaceClass;

    protected List<RegistryConfig> registries;

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public S interfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
        return (S) this;
    }


    public S registryConfig(RegistryConfig registryConfig) {
        if(registries==null || registries.isEmpty()){
            registries = new ArrayList<>();
        }
        registries.add(registryConfig);
        return (S) this;
    }

    public S registryConfig(List<RegistryConfig> registryConfig) {
        registries = registryConfig;
        return (S) this;
    }
}
