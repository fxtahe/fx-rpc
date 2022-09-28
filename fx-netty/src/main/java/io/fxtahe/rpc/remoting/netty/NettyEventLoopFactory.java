package io.fxtahe.rpc.remoting.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ThreadFactory;

/**
 * @author fxtahe
 * @since 2022/8/25 15:38
 */
public class NettyEventLoopFactory {


    private static final String OS_NAME = "os.name";

    private static final String OS_LINUX_PREFIX = "linux";


    public static EventLoopGroup buildEventLoopGroup(int threads,String threadFactoryName){
        ThreadFactory threadFactory = new DefaultThreadFactory(threadFactoryName, true);
        return shouldEpoll() ? new EpollEventLoopGroup(threads,threadFactory) : new NioEventLoopGroup(threads,threadFactory);
    }

    public static Class<? extends SocketChannel> socketChannelClass() {
        return shouldEpoll() ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    public static Class<? extends ServerSocketChannel> serverSocketChannelClass() {
        return shouldEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    public static boolean shouldEpoll(){
        String osName = System.getProperty(OS_NAME);
        return osName.toLowerCase().contains(OS_LINUX_PREFIX) && Epoll.isAvailable();
    }

}
