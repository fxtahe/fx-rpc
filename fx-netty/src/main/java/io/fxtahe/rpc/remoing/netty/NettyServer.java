package io.fxtahe.rpc.remoing.netty;

import io.fxtahe.rpc.common.remoting.ConnectionHandler;
import io.fxtahe.rpc.common.remoting.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author fxtahe
 * @since 2022/8/25 15:28
 */
public class NettyServer implements Server {


    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);


    final static EventLoopGroup boss = NettyEventLoopFactory.buildEventLoopGroup(1);
    final static EventLoopGroup worker = NettyEventLoopFactory.buildEventLoopGroup(Runtime.getRuntime().availableProcessors());

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

    private List<NettyConnection> connections;

    private InetSocketAddress socketAddress;

    public NettyServer(InetSocketAddress socketAddress, ConnectionHandler connectionHandler) {
        this.socketAddress = socketAddress;
        this.connectionHandler = connectionHandler;
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
                        //TODO add protocol decoder encoder
                        .addLast("server-idle-handler", new IdleStateHandler(0, 0, 1000, MILLISECONDS))
                        .addLast("server-handler", new NettyServerHandler(connectionHandler));
            }
        });
    }


    @Override
    public void start() {
        if (!started) {
            ChannelFuture channelFuture = bootstrap.bind(socketAddress).syncUninterruptibly();
            channel = channelFuture.channel();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            }));
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
