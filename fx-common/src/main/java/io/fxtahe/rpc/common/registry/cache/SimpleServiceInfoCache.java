package io.fxtahe.rpc.common.registry.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import io.fxtahe.rpc.common.registry.ServiceInstance;
import io.fxtahe.rpc.common.util.FileUtil;
import io.fxtahe.rpc.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author fxtahe
 * @since 2022/8/24 17:40
 */
public class SimpleServiceInfoCache implements ServiceInfoCache {

    public static final Logger log = LoggerFactory.getLogger(SimpleServiceInfoCache.class);

    private String registryType;

    private String diskDirPath = System.getProperty("user.home") + "/fx-registry/";

    private final Map<String, List<ServiceInstance>> instances = new ConcurrentHashMap<>();

    private final ExecutorService refreshThreadPool = new ThreadPoolExecutor(10, 20, 2000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(20), runnable -> {
        Thread thread = new Thread(runnable,"refresh service info thread");
        thread.setDaemon(true);
        return thread;
    });

    private final Map<String,RefreshDiskRunnable> refreshDiskRunnableMap = new ConcurrentHashMap<>();

    public SimpleServiceInfoCache(boolean failOver,String registryType,String cachePath) {
        this.registryType = registryType;
        this.diskDirPath = (cachePath==null||cachePath.length()==0 ?diskDirPath:cachePath)+registryType;
        if(failOver){
            log.info("open fail-over pattern");
            Future<Map<String, List<ServiceInstance>>> submit = refreshThreadPool.submit(new LoadDiskRunnable(diskDirPath));
            try {
                Map<String, List<ServiceInstance>> result = submit.get();
                instances.putAll(result);
            } catch (InterruptedException | ExecutionException ignored) {
            }
        }
    }

    @Override
    public void shutdown() throws Exception {
        refreshThreadPool.shutdown();
        if(refreshThreadPool.awaitTermination(2000,TimeUnit.MILLISECONDS)){
            refreshThreadPool.shutdownNow();
        }
        instances.clear();
    }


    @Override
    public void refreshInstances(String serviceId, List<ServiceInstance> instances) {
        if(instances ==null || instances.size()==0){
            this.instances.remove(serviceId);
            this.refreshDiskRunnableMap.remove(serviceId);
        }else{
            log.info("refresh {} cache of {} ",serviceId,registryType);
            this.instances.put(serviceId, instances);
            RefreshDiskRunnable refreshRunnable = refreshDiskRunnableMap.computeIfAbsent(serviceId, (key) -> new RefreshDiskRunnable(serviceId, instances, diskDirPath));
            refreshThreadPool.execute(refreshRunnable);
        }
    }


    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        return instances.get(serviceId);
    }


    private static class RefreshDiskRunnable implements Runnable {

        private final String diskPath;

        private final String serviceId;

        private final List<ServiceInstance> serviceInstances;


        public RefreshDiskRunnable(String serviceId, List<ServiceInstance> serviceInstances,String diskPath) {
            this.serviceId = serviceId;
            this.serviceInstances = serviceInstances;
            this.diskPath = diskPath;
        }

        @Override
        public void run() {
            String content="";
            String path = diskPath+"/"+serviceId+".cache";
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


    private static class LoadDiskRunnable implements Callable<Map<String,List<ServiceInstance>>>{


        private final String diskPath;

        public LoadDiskRunnable(String diskPath) {
            this.diskPath = diskPath;
        }

        @Override
        public Map<String,List<ServiceInstance>> call() throws Exception {
            log.info("load service info from {} disk path",diskPath);
            Map<String,List<ServiceInstance>> instances = new ConcurrentHashMap<>();
            Map<String,String> caches = FileUtil.readFilesContent(diskPath, "cache", "UTF-8");
            if(caches.isEmpty()){
                log.warn("cannot load service info from {} disk path",diskPath);
            }
            TypeReference<List<ServiceInstance>> typeReference = new TypeReference<List<ServiceInstance>>() {
            };
            for(Map.Entry<String,String> cache:caches.entrySet()){
                try{
                    String serviceId = cache.getKey();
                    List<ServiceInstance> serviceInstances = JsonUtil.readJsonString(cache.getValue(), typeReference);
                    instances.put(serviceId,serviceInstances);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return instances;
        }
    }
}
