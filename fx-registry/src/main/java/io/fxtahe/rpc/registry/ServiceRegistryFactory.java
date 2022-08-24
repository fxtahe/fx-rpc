package io.fxtahe.rpc.registry;

import io.fxtahe.rpc.common.config.RegistryConfig;
import io.fxtahe.rpc.registry.cache.CacheServiceRegistry;
import io.fxtahe.rpc.registry.zookeeper.ZookeeperServiceRegistry;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author fxtahe
 * @since 2022/8/24 17:40
 */
public class ServiceRegistryFactory {


    public static ServiceRegistry buildRegistry(RegistryConfig registryConfig) throws InterruptedException {
        String registryType = registryConfig.getRegistryType();
        String connectionString = registryConfig.getConnectionString();
        int connectTimeout = registryConfig.getConnectTimeout();
        int readTimeout = registryConfig.getReadTimeout();
        switch (registryType){
            case "zookeeper":
                CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().
                        connectString(connectionString)
                        .connectionTimeoutMs(connectTimeout)
                        .sessionTimeoutMs(readTimeout)
                        .retryPolicy(new ExponentialBackoffRetry(1000, 10, 200))
                        .build();
                // start()开始连接，没有此会报错
                curatorFramework.start();
                // 阻塞直到连接成功
                curatorFramework.blockUntilConnected(1000, TimeUnit.MILLISECONDS);
                ServiceRegistry serviceRegistry = new ZookeeperServiceRegistry(curatorFramework);
                if(registryConfig.isUseCache()){
                    serviceRegistry = new CacheServiceRegistry(serviceRegistry);
                }
                return serviceRegistry;
            default:
                return null;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setConnectionString("127.0.0.1:2181");
        registryConfig.setConnectTimeout(60000);
        registryConfig.setReadTimeout(60000);
        registryConfig.setRegistryType("zookeeper");
        registryConfig.setUseCache(false);
        ServiceRegistry serviceRegistry = buildRegistry(registryConfig);
        Subscriber subscriber = new Subscriber();
        subscriber.setServiceId("io.fxtahe.rpc.common.filter.Filter");
        subscriber.setServiceListener((serviceId, serviceInstances, newState) -> System.out.println(serviceId+":"+newState));
        serviceRegistry.subscribe(subscriber);

        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setServiceId("io.fxtahe.rpc.common.filter.Filter");
        serviceInstance.setHost("127.0.0.1");
        serviceInstance.setPort(8080);
        serviceInstance.setId("127.0.0.1:8080");
        serviceRegistry.register(serviceInstance);

        ServiceInstance serviceInstance1 = new ServiceInstance();
        serviceInstance1.setServiceId("io.fxtahe.rpc.common.filter.Filter");
        serviceInstance1.setHost("127.0.0.1");
        serviceInstance1.setPort(8080);
        serviceInstance1.setId("127.0.0.1:8081");
        serviceRegistry.register(serviceInstance1);
        new CountDownLatch(1).await();


    }

}
