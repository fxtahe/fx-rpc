package io.fxtahe.rpc.remoing.netty;

import io.fxtahe.rpc.common.remoting.ConnectionHandler;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author fxtahe
 * @since 2022/8/25 16:02
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelDuplexHandler {


    private ConnectionHandler connectionHandler;


    public NettyServerHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyConnection connection = NettyConnectionManager.putIfAbsent(ctx.channel());
        connectionHandler.connect(connection);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyConnection connection = NettyConnectionManager.putIfAbsent(ctx.channel());
        connectionHandler.disConnect(connection);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyConnection connection = NettyConnectionManager.putIfAbsent(ctx.channel());
        connectionHandler.received(connection,msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //remove connection when don't receive any heartbeat from client until timeout
        if(evt instanceof IdleStateEvent){
            NettyConnection connection = NettyConnectionManager.putIfAbsent(ctx.channel());
            try{
                connection.close();
            }finally {
                NettyConnectionManager.removeIfDisconnected(ctx.channel());
            }
        }else{
            super.userEventTriggered(ctx,evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        NettyConnection connection = NettyConnectionManager.putIfAbsent(ctx.channel());
        try{
            connectionHandler.caught(connection,cause);
        }finally {
            NettyConnectionManager.removeIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        NettyConnection connection = NettyConnectionManager.putIfAbsent(ctx.channel());
        connectionHandler.send(connection,msg);
    }
}
