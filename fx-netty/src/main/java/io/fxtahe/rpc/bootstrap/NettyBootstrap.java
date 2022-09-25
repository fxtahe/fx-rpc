package io.fxtahe.rpc.bootstrap;

import io.fxtahe.rpc.common.bootstrap.BootStrap;
import io.fxtahe.rpc.common.config.ServerConfig;
import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.core.Result;
import io.fxtahe.rpc.common.core.RpcRequest;
import io.fxtahe.rpc.common.core.RpcResponse;
import io.fxtahe.rpc.common.costants.StatusConstants;
import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.future.FutureManager;
import io.fxtahe.rpc.common.future.RpcFuture;
import io.fxtahe.rpc.common.invoke.ConsumerClientInvoker;
import io.fxtahe.rpc.common.invoke.Invoker;
import io.fxtahe.rpc.common.registry.ServiceInstance;
import io.fxtahe.rpc.common.remoting.Client;
import io.fxtahe.rpc.common.remoting.Connection;
import io.fxtahe.rpc.common.remoting.ConnectionHandler;
import io.fxtahe.rpc.common.remoting.Server;
import io.fxtahe.rpc.common.util.ClassUtil;
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
@Extension(alias = "netty" ,singleton = false)
public class NettyBootstrap implements BootStrap {

    public static final Logger log = LoggerFactory.getLogger(NettyBootstrap.class);

    private final Map<String,Invoker> exporterCache;

    private final Map<String,Server> serverCache;

    private final Map<String,Client> referClientCache;
    //TODO split into tow types for request and response
    private ConnectionHandler connectionHandler = new ConnectionHandler() {
        @Override
        public void received(Connection connection, Object message) {
            if(message instanceof RpcRequest){
                RpcRequest rpcRequest = (RpcRequest) message;
                long id = rpcRequest.getId();
                Object data = rpcRequest.getData();
                RpcResponse rpcResponse = new RpcResponse();
                rpcResponse.setId(id);
                rpcResponse.setSerializationName(rpcRequest.getSerializationName());
                try{
                    Invocation invocation = (Invocation) data;
                    // TODO It shouldn't be handled here
                    invocation.setParameterTypes(ClassUtil.getClasses(invocation.getParameterTypesDesc()));
                    String interfaceName = invocation.getInterfaceName();
                    Invoker invoker = exporterCache.get(interfaceName);
                    Result result = invoker.invoke(invocation);
                    if (result.hasException()){
                        rpcResponse.setStatus(StatusConstants.BAD_RESPONSE);
                    }
                    rpcResponse.setData(result);
                }catch (Throwable t){
                    rpcResponse.setStatus(StatusConstants.BAD_RESPONSE);
                    rpcResponse.setErrorMsg(t.getMessage());
                }
                connection.send(rpcResponse);
            }else if(message instanceof RpcResponse){
                RpcResponse rpcResponse = (RpcResponse) message;
                long id = rpcResponse.getId();
                RpcFuture rpcFuture = FutureManager.removeFuture(id);
                rpcFuture.received(rpcResponse);
            }
        }
    };


    public NettyBootstrap() {
        referClientCache = new ConcurrentHashMap<>(16);
        exporterCache = new ConcurrentHashMap<>(16);
        serverCache = new ConcurrentHashMap<>(4);
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
        return new ConsumerClientInvoker(client,serviceInstance);
    }

    @Override
    public void unRefer(String serviceId) {
        Client client = referClientCache.remove(serviceId);
        if(client!=null && !client.isClosed()){
            client.close();
        }
    }
}
