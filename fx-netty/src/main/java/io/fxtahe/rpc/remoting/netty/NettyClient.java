package io.fxtahe.rpc.remoting.netty;

import io.fxtahe.rpc.common.remoting.Client;
import io.fxtahe.rpc.common.remoting.ConnectionHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author fxtahe
 * @since 2022/9/6 9:07
 */
public class NettyClient implements Client {

    private Bootstrap bootstrap;

    private EventLoopGroup eventLoopGroup;

    private InetSocketAddress remoteAddress;

    private Channel channel;

    private volatile boolean closed;

    private final Lock connectLock = new ReentrantLock();

    public NettyClient(String host, int port, ConnectionHandler connectionHandler) {
        this.remoteAddress = new InetSocketAddress(host,port);
        eventLoopGroup = NettyEventLoopFactory.buildEventLoopGroup(Runtime.getRuntime().availableProcessors());
        bootstrap = new Bootstrap().group(eventLoopGroup)
                .channel(NettyEventLoopFactory.socketChannelClass())
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("decoder",new NettyDecoder())
                                .addLast("encoder",new NettyEncoder())
                                .addLast("client-idle-handler",new IdleStateHandler(60000,0,0, TimeUnit.MILLISECONDS))
                                .addLast("cilent-handler",new NettyClientHandler(connectionHandler));
                    }
                });
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            eventLoopGroup.shutdownGracefully();
        }));
    }

    @Override
    public void connect() {
        connectLock.lock();
        try{
            ChannelFuture channelFuture = bootstrap.connect(remoteAddress).syncUninterruptibly();
            Channel newChannel = channelFuture.channel();
            if(this.channel !=null){
                this.channel.close().syncUninterruptibly();
                NettyConnectionManager.removeIfDisconnected(this.channel);
            }
            this.channel = newChannel;
        }finally {
            connectLock.unlock();
        }
    }

    @Override
    public void reConnect() {
        connectLock.lock();
        try{
            disConnect();
            connect();
        }finally {
            connectLock.unlock();
        }
    }

    @Override
    public void send(Object message) {
        if(!isConnected()){
            connect();
        }
        NettyConnection connection = NettyConnectionManager.putIfAbsent(channel);
        connection.send(message);
    }

    @Override
    public void disConnect() {
        connectLock.lock();
        try{
            channel.close().syncUninterruptibly();
            NettyConnectionManager.removeIfDisconnected(channel);
        }finally {
            connectLock.unlock();
        }

    }

    @Override
    public boolean isConnected() {
        NettyConnection connection = NettyConnectionManager.putIfAbsent(channel);
        return connection!=null && !connection.isClosed();
    }

    @Override
    public void close() {
        if(!closed){
            connectLock.lock();
            try{
                disConnect();
            }finally {
                eventLoopGroup.shutdownGracefully();
                connectLock.unlock();
            }
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }
}
