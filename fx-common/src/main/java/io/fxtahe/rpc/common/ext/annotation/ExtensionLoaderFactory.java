package io.fxtahe.rpc.common.ext.annotation;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author fxtahe
 * @since 2022/8/18 17:04
 */
public class ExtensionLoaderFactory {


    private static final ConcurrentHashMap<Class<?>,ExtensionLoader<?>> EXTENSIONS = new ConcurrentHashMap<>();

    @SuppressWarnings(value = {"unchecked"})
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> clazz){
        ExtensionLoader<T> extensionLoader = (ExtensionLoader<T>) EXTENSIONS.get(clazz);
        if(extensionLoader==null){
            synchronized (ExtensionLoaderFactory.class){
                extensionLoader = (ExtensionLoader<T>) EXTENSIONS.get(clazz);
                if(extensionLoader ==null){
                    extensionLoader = new ExtensionLoader<>(clazz);
                    EXTENSIONS.put(clazz,extensionLoader);
                }
            }
        }
        return extensionLoader;
    }

}
