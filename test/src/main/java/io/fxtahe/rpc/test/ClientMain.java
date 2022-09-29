package io.fxtahe.rpc.test;

import io.fxtahe.rpc.common.config.ConsumerConfig;
import io.fxtahe.rpc.common.config.RegistryConfig;
import io.fxtahe.rpc.common.context.RpcContext;
import io.fxtahe.rpc.common.core.RpcRequest;
import io.fxtahe.rpc.common.core.RpcResponse;
import io.fxtahe.rpc.common.costants.InvokeTypeEnum;
import io.fxtahe.rpc.common.remoting.Connection;
import io.fxtahe.rpc.common.remoting.ConnectionHandler;
import io.fxtahe.rpc.common.serialize.SerializationEnum;
import io.fxtahe.rpc.common.util.IdGenerator;
import io.fxtahe.rpc.remoting.netty.NettyClient;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author fxtahe
 * @since 2022-09-28 21:21
 */
public class ClientMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setConnectionString("127.0.0.1:2181");
        registryConfig.setConnectTimeout(60000);
        registryConfig.setReadTimeout(60000);
        ConsumerConfig<HelloService> consumerConfig = new ConsumerConfig<>();
        consumerConfig.registryConfig(registryConfig);
        consumerConfig.interfaceClass(HelloService.class);
        consumerConfig.setInvokeType(InvokeTypeEnum.SYNC);
        HelloService refer = consumerConfig.refer();
        String s = refer.sayHello(1);
        System.out.println(s);

    }
}
