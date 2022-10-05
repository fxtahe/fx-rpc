package io.fxtahe.rpc.test;

import io.fxtahe.rpc.common.config.ConsumerConfig;
import io.fxtahe.rpc.common.config.RegistryConfig;
import io.fxtahe.rpc.common.context.RpcContext;
import io.fxtahe.rpc.common.costants.InvokeTypeEnum;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author fxtahe
 * @since 2022-09-28 21:21
 */
public class ClientMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setConnectionString("127.0.0.1:2182");
        registryConfig.setConnectTimeout(60000);
        registryConfig.setReadTimeout(60000);
        ConsumerConfig<HelloService> consumerConfig = new ConsumerConfig<>();
        consumerConfig.registryConfig(registryConfig);
        consumerConfig.interfaceClass(HelloService.class);
        consumerConfig.setInvokeType(InvokeTypeEnum.ASYNC);
        consumerConfig.setTimeOut(4000);
        HelloService refer = consumerConfig.refer();
        String s = refer.sayHello(1);
        CompletableFuture<String> future = RpcContext.getContext().getFuture();
        String s1 = future.get();
        System.out.println(s);

    }
}
