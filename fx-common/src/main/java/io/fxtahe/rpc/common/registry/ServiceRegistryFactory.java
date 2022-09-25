package io.fxtahe.rpc.common.registry;

import io.fxtahe.rpc.common.config.RegistryConfig;
import io.fxtahe.rpc.common.ext.ExtensionLoaderFactory;
import io.fxtahe.rpc.common.registry.cache.CacheServiceRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fxtahe
 * @since 2022/8/24 17:40
 */
public class ServiceRegistryFactory {

    private static final Map<RegistryConfig, ServiceRegistry> serviceRegistryCache = new ConcurrentHashMap<>(4);

    public static ServiceRegistry buildRegistry(RegistryConfig registryConfig) {
        ServiceRegistry serviceRegistry = serviceRegistryCache.computeIfAbsent(registryConfig, (config) -> ExtensionLoaderFactory.getExtensionLoader(ServiceRegistry.class).getInstance(config.getRegistryType(), new Class[]{RegistryConfig.class}, new Object[]{config}));
        if(registryConfig.isUseCache()){
            serviceRegistry = new CacheServiceRegistry(serviceRegistry);
        }
        return serviceRegistry;

    }


}
