package io.fxtahe.rpc.test;


import io.fxtahe.rpc.common.config.RegistryConfig;
import io.fxtahe.rpc.common.config.ServerConfig;
import io.fxtahe.rpc.common.config.ProviderConfig;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author fxtahe
 * @since 2022-09-28 21:21
 */
public class ApplicationMain {

    public static void main(String[] args) throws InterruptedException {


        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(9000);
        serverConfig.setHost("127.0.0.1");
        serverConfig.setServerType("netty");

        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setConnectionString("127.0.0.1:2181");
        registryConfig.setConnectTimeout(6000);
        registryConfig.setReadTimeout(6000);

        ExecutorService executorService = Executors.newFixedThreadPool(10,(r)->{
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("user-thread");
            return thread;
        });

        ProviderConfig<HelloService> providerConfig = new ProviderConfig<>();
        providerConfig.registryConfig(registryConfig);
        providerConfig.interfaceClass(HelloService.class);
        providerConfig.setRef(new HelloServiceImpl());
        providerConfig.setBootStrap(serverConfig);
        providerConfig.setExecutor(executorService);
        providerConfig.export();
        new CountDownLatch(1).await();




    }
}
