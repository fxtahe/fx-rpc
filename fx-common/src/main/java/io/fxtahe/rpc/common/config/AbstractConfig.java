package io.fxtahe.rpc.common.config;

/**
 * @author fxtahe
 * @since 2022/8/19 15:03
 */
public abstract class AbstractConfig<T,S extends AbstractConfig<T, S>> {

    protected Class<T> interfaceClass;

    protected RegistryConfig registryConfig;

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public S interfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
        return (S) this;
    }

    public RegistryConfig getRegistryConfig() {
        return registryConfig;
    }

    public S registryConfig(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
        return (S) this;
    }
}
