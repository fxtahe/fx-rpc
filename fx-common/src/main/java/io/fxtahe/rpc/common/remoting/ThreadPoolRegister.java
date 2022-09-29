package io.fxtahe.rpc.common.remoting;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author fxtahe
 * @since 2022/9/29 9:57
 */
public class ThreadPoolRegister {

    /**
     * user customer service request thread pool
     */
    private static final Map<String, ExecutorService> threadPoolCache = new ConcurrentHashMap<>();

    /**
     * default request handler thread pool
     */
    private static ThreadPoolExecutor defaultPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() / 2, Runtime.getRuntime().availableProcessors(), 2000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(200), runnable -> {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.setName("default-thread-handler");
        return thread;
    });


    public static ExecutorService selectThreadPool(String serviceId){
        return threadPoolCache.getOrDefault(serviceId,defaultPool);
    }

    public static void registerThreadPool(String serviceId,ExecutorService executorService){
        threadPoolCache.put(serviceId,executorService);
    }

    public static void unRegisterThreadPool(String serviceId){
        threadPoolCache.remove(serviceId);
    }

}
