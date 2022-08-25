package io.fxtahe.rpc.registry.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.*;
import org.apache.curator.x.discovery.details.ServiceCacheListener;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author fxtahe
 * @since 2022-08-20 22:06
 */
public class ZookeeperRegistryTest {


    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().
                connectString("127.0.0.1:2181")
                .retryPolicy(new ExponentialBackoffRetry(1000, 3, 200))
                .build();
        curatorFramework.getConnectionStateListenable().addListener((client, newState) -> {
                    if (newState == ConnectionState.RECONNECTED) {
                        System.out.println("重连成功！");
                    }
                }
        );

        // start()开始连接，没有此会报错
        curatorFramework.start();
        // 阻塞直到连接成功
        curatorFramework.blockUntilConnected();
        ServiceDiscovery serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
                .client(curatorFramework)
                .basePath("/fx-rpc")
                .build();

        ServiceInstance build = ServiceInstance.builder()
                .serviceType(ServiceType.DYNAMIC).address("127.0.0.1").id("1")
                .name("io.fxtahe.rpc.common.filter.Filter").port(8080).payload(new ServiceMeta()).build();


        ServiceCache cache = serviceDiscovery.serviceCacheBuilder().name("io.fxtahe.rpc.common.filter.Filter").build();
        cache.addListener(new ServiceCacheListener() {
            @Override
            public void cacheChanged() {
                System.out.println("节点变更");
                List<ServiceInstance> instances = cache.getInstances();
                System.out.println(instances.size());
            }

            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {

            }
        });

        cache.start();


        serviceDiscovery.registerService(build);
        ServiceInstance build1 = ServiceInstance.builder()
                .serviceType(ServiceType.DYNAMIC).address("127.0.0.1").id("2")
                .name("io.fxtahe.rpc.common.filter.Filter").port(8081).payload(new ServiceMeta()).build();
        serviceDiscovery.registerService(build1);

        serviceDiscovery.unregisterService(build);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await();

    }

}
