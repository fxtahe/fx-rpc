package io.fxtahe.rpc.common.bootstrap;

import io.fxtahe.rpc.common.config.ServerConfig;
import io.fxtahe.rpc.common.ext.ExtensionLoaderFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fxtahe
 * @since 2022/9/14 11:28
 */
public class BootStrapFactory {

    private static final Map<String,BootStrap> bootStrapCache = new ConcurrentHashMap<>();


    public static BootStrap buildBootStrap(String bootStrapType){
        return bootStrapCache.computeIfAbsent(bootStrapType, (key) ->
                ExtensionLoaderFactory.getExtensionLoader(BootStrap.class).getInstance(key)
        );
    }



}
