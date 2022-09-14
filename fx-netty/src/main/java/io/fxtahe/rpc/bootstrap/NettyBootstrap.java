package io.fxtahe.rpc.bootstrap;

import io.fxtahe.rpc.common.bootstrap.BootStrap;
import io.fxtahe.rpc.common.config.BootStrapConfig;
import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.core.Result;
import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.invoke.Invoker;
import io.fxtahe.rpc.common.remoting.Connection;
import io.fxtahe.rpc.common.remoting.ConnectionHandler;
import io.fxtahe.rpc.common.remoting.Server;
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

    private Map<String,Invoker> exporterCache;

    private BootStrapConfig bootStrapConfig;

    private Server server;

    private ConnectionHandler connectionHandler = new ConnectionHandler() {
        @Override
        public void received(Connection connection, Object message) {
            if(message instanceof Invocation){
                Invocation invocation = (Invocation) message;
                String interfaceName = invocation.getInterfaceName();
                Invoker invoker = exporterCache.get(interfaceName);
                Result invoke = invoker.invoke(invocation);
                connection.send(invoke);
            }
        }
    };


    public NettyBootstrap(BootStrapConfig bootStrapConfig) {
        this.bootStrapConfig = bootStrapConfig;
        exporterCache = new ConcurrentHashMap<>();
        server = new NettyServer(bootStrapConfig.getHost(), bootStrapConfig.getPort(), connectionHandler);
    }

    @Override
    public void export(Invoker invoker) {
        exporterCache.put(invoker.getInterface().getName(),invoker);
        server.start();
    }

    @Override
    public void unExport(Invoker invoker) {
        exporterCache.remove(invoker.getInterface().getName());
    }

    @Override
    public Invoker refer() {


        return null;
    }

    @Override
    public void unRefer() {


    }

}