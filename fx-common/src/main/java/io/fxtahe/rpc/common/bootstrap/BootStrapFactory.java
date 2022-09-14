package io.fxtahe.rpc.common.bootstrap;

import io.fxtahe.rpc.common.config.BootStrapConfig;
import io.fxtahe.rpc.common.ext.ExtensionLoaderFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fxtahe
 * @since 2022/9/14 11:28
 */
public class BootStrapFactory {

    private static final Map<BootStrapConfig,BootStrap> bootStrapCache = new ConcurrentHashMap<>();


    public static BootStrap buildBootStrap(BootStrapConfig bootStrapConfig){
        return bootStrapCache.computeIfAbsent(bootStrapConfig, (config) ->
                ExtensionLoaderFactory.getExtensionLoader(BootStrap.class).getInstance(config.getServerType(), new Class[]{BootStrapConfig.class}, new Object[]{config})
        );
    }



}
