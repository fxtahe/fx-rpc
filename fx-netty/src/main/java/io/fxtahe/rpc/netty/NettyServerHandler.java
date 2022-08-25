package io.fxtahe.rpc.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.net.SocketAddress;

/**
 * @author fxtahe
 * @since 2022/8/25 16:02
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelDuplexHandler {


    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {


    }
}
