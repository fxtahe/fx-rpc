package io.fxtahe.rpc.common.registry.cache;

import io.fxtahe.rpc.common.registry.ServiceInstance;
import io.fxtahe.rpc.common.util.FileUtil;
import io.fxtahe.rpc.common.util.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author fxtahe
 * @since 2022/8/24 17:40
 */
public class SimpleServiceInfoCache implements ServiceInfoCache {

    private final Map<String, List<ServiceInstance>> instances = new ConcurrentHashMap<>();

    private final ExecutorService refreshThreadPool = new ThreadPoolExecutor(10, 20, 2000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(20), runnable -> {
        Thread thread = new Thread(runnable,"refresh service info thread");
        thread.setDaemon(true);
        return thread;
    });


    @Override
    public void shutdown() throws Exception {
        refreshThreadPool.shutdown();
        if(refreshThreadPool.awaitTermination(2000,TimeUnit.MILLISECONDS)){
            refreshThreadPool.shutdownNow();
        }
    }


    @Override
    public void refreshInstances(String serviceId, List<ServiceInstance> instances) {
        this.instances.put(serviceId, instances);
        RefreshDiskRunnable refreshRunnable = new RefreshDiskRunnable(serviceId, instances);
        refreshThreadPool.execute(refreshRunnable);
    }


    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        return instances.get(serviceId);
    }


    private static class RefreshDiskRunnable implements Runnable {

        private final String serviceId;

        private final List<ServiceInstance> serviceInstances;

        private final String diskDirPath = System.getProperty("user.home") + "/fx-registry/";

        public RefreshDiskRunnable(String serviceId, List<ServiceInstance> serviceInstances) {
            this.serviceId = serviceId;
            this.serviceInstances = serviceInstances;
        }

        @Override
        public void run() {
            String content="";
            String path = diskDirPath+serviceId+".cache";
            if(serviceInstances !=null && !serviceInstances.isEmpty()){
                content = JsonUtil.writeJson(serviceInstances);
            }
            try {
                FileUtil.writeConcurrentFileContent(content,path,"UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
