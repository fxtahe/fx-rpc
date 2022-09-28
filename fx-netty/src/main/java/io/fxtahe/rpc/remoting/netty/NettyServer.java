package io.fxtahe.rpc.remoting.netty;

import io.fxtahe.rpc.common.remoting.ConnectionHandler;
import io.fxtahe.rpc.common.remoting.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author fxtahe
 * @since 2022/8/25 15:28
 */
public class NettyServer implements Server {


    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);


    final static EventLoopGroup boss = NettyEventLoopFactory.buildEventLoopGroup(1,"netty-server-boss");
    final static EventLoopGroup worker = NettyEventLoopFactory.buildEventLoopGroup(Runtime.getRuntime().availableProcessors(),"netty-server-worker");

    private ServerBootstrap bootstrap;
    /**
     * boss channel dispatch connect
     */
    private Channel channel;

    /**
     * the connection handler
     */
    private ConnectionHandler connectionHandler;

    private volatile boolean started;

    private InetSocketAddress socketAddress;

    public NettyServer(String host, int port, ConnectionHandler connectionHandler) {
        this.socketAddress = new InetSocketAddress(host,port);
        this.connectionHandler = connectionHandler;
        init();
    }

    public void init() {
        bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker);
        bootstrap.channel(NettyEventLoopFactory.serverSocketChannelClass());
        bootstrap.option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast("decoder",new NettyDecoder())
                        .addLast("encoder",new NettyEncoder())
                        .addLast("server-idle-handler", new IdleStateHandler(0, 0, 100000, MILLISECONDS))
                        .addLast("server-handler", new NettyServerHandler(connectionHandler));
            }
        });
    }


    @Override
    public void start() {
        if (!started) {
            ChannelFuture channelFuture = bootstrap.bind(socketAddress).syncUninterruptibly();
            channel = channelFuture.channel();
            started=true;
        }

    }

    @Override
    public void close() {
        if (channel != null) {
            channel.close();
        }
        if (bootstrap != null) {
            boss.shutdownGracefully().syncUninterruptibly();
            worker.shutdownGracefully().syncUninterruptibly();
        }
        NettyConnectionManager.getConnections().forEach(NettyConnection::close);
    }


}
