package io.fxtahe.rpc.registry.cache;

import io.fxtahe.rpc.registry.ServiceInstance;
import io.fxtahe.rpc.registry.Subscriber;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author fxtahe
 * @since 2022/8/24 17:40
 */
public class SimpleServiceInfoCache implements ServiceInfoCache {

    private final Map<String, List<ServiceInstance>> registerInstances = new ConcurrentHashMap<>();

    private final Map<String, List<Subscriber>> subscribers = new ConcurrentHashMap<>();

    private final Map<String, List<ServiceInstance>> remoteInstances = new ConcurrentHashMap<>();

    private Semaphore semaphore = new Semaphore(1);

    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1, r -> {
        Thread thread = new Thread("refresh service info thread");
        thread.setDaemon(true);
        return thread;
    });

    @Override
    public void shutdown() throws Exception {
        registerInstances.clear();
        subscribers.clear();
        registerInstances.clear();
    }


    @Override
    public void refreshInstances(String serviceId, List<ServiceInstance> instances) {
        registerInstances.put(serviceId, instances);
    }


    private class RefreshRunnable implements Runnable {


        @Override
        public void run() {

        }
    }
}
