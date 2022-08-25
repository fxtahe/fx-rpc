package io.fxtahe.rpc.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author fxtahe
 * @since 2022/8/25 15:28
 */
public class NettyServer {

    final static EventLoopGroup boss = NettyEventLoopFactory.buildEventLoopGroup(1);
    final static EventLoopGroup worker = NettyEventLoopFactory.buildEventLoopGroup(Runtime.getRuntime().availableProcessors());

    public static void main(String[] args) {


        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss,worker);
        bootstrap.channel(NettyEventLoopFactory.serverSocketChannelClass());

        bootstrap.option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline()
                        .addLast("server-idle-handler", new IdleStateHandler(0, 0, 1000, MILLISECONDS));
            }
        });

        ChannelFuture channelFuture = bootstrap.bind(8080).syncUninterruptibly();
        Channel channel = channelFuture.channel();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }));

    }
}
