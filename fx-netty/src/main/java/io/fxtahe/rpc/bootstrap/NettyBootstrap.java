package io.fxtahe.rpc.bootstrap;

import io.fxtahe.rpc.common.bootstrap.BootStrap;
import io.fxtahe.rpc.common.config.ServerConfig;
import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.core.Result;
import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.filter.FilterChainBuilder;
import io.fxtahe.rpc.common.handler.RequestProcessHandler;
import io.fxtahe.rpc.common.invoke.ConsumerClientInvoker;
import io.fxtahe.rpc.common.invoke.Invoker;
import io.fxtahe.rpc.common.registry.ServiceInstance;
import io.fxtahe.rpc.common.remoting.Client;
import io.fxtahe.rpc.common.remoting.ConnectionHandler;
import io.fxtahe.rpc.common.remoting.DefaultConnectionHandler;
import io.fxtahe.rpc.common.remoting.Server;
import io.fxtahe.rpc.remoting.netty.NettyClient;
import io.fxtahe.rpc.remoting.netty.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fxtahe
 * @since 2022/9/13 14:49
 */
@Extension(alias = "netty")
public class NettyBootstrap implements BootStrap {

    public static final Logger log = LoggerFactory.getLogger(NettyBootstrap.class);

    private final Map<String,Invoker> exporterCache;

    private final Map<String,Server> serverCache;

    private final Map<String,Client> referClientCache;

    private final ConnectionHandler connectionHandler;

    private  RequestProcessHandler requestProcessHandler = new RequestProcessHandler() {
        @Override
        public Result handleRequest(Invocation invocation) {
            log.info("handle {} request thread {}",invocation.getInterfaceName(),Thread.currentThread().getName());
            String interfaceName = invocation.getInterfaceName();
            Invoker invoker = exporterCache.get(interfaceName);
            return invoker.invoke(invocation);
        }
    };


    public NettyBootstrap() {
        referClientCache = new ConcurrentHashMap<>(16);
        exporterCache = new ConcurrentHashMap<>(16);
        serverCache = new ConcurrentHashMap<>(4);
        connectionHandler  = new DefaultConnectionHandler(requestProcessHandler);
    }

    @Override
    public void export(Invoker invoker,ServerConfig serverConfig) {
        exporterCache.put(invoker.getInterfaceName(),invoker);
        Server server = serverCache.computeIfAbsent(serverConfig.getAddress(), (key)->new NettyServer(serverConfig.getHost(),serverConfig.getPort(),connectionHandler));
        server.start();
    }

    @Override
    public void unExport(Invoker invoker) {
        exporterCache.remove(invoker.getInterfaceName());
    }

    @Override
    public Invoker refer(ServiceInstance serviceInstance) {
        Client client = referClientCache.computeIfAbsent(serviceInstance.getServiceId(), (key) -> new NettyClient(serviceInstance.getHost(), serviceInstance.getPort(), connectionHandler));
        ConsumerClientInvoker consumerClientInvoker = new ConsumerClientInvoker(client, serviceInstance);
        return FilterChainBuilder.buildFilterChain(consumerClientInvoker, "consumer");
    }

    @Override
    public void unRefer(String serviceId) {
        Client client = referClientCache.remove(serviceId);
        if(client!=null && !client.isClosed()){
            client.close();
        }
    }
}
