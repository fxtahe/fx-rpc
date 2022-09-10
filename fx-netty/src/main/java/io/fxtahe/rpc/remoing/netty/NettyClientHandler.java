package io.fxtahe.rpc.remoing.netty;


import io.fxtahe.rpc.common.core.RpcRequest;
import io.fxtahe.rpc.common.core.RpcResponse;
import io.fxtahe.rpc.common.costants.StatusConstants;
import io.fxtahe.rpc.common.remoting.ConnectionHandler;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fxtahe
 * @since 2022/9/5 16:15
 */
public class NettyClientHandler extends ChannelDuplexHandler {

    public static final Logger log = LoggerFactory.getLogger(NettyClientHandler.class);

    private ConnectionHandler connectionHandler;

    public NettyClientHandler(ConnectionHandler connectionHandler) {
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
        try{
            connectionHandler.disConnect(connection);
        }finally {
            NettyConnectionManager.removeIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyConnection connection = NettyConnectionManager.putIfAbsent(ctx.channel());
        connectionHandler.received(connection,msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            try{
                NettyConnection connection = NettyConnectionManager.putIfAbsent(ctx.channel());
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setHeartBeat(true);
                rpcRequest.setTwoWay(true);
                connectionHandler.send(connection,rpcRequest);
            }finally {
                NettyConnectionManager.removeIfDisconnected(ctx.channel());
            }
        }else{
            super.userEventTriggered(ctx,evt);
        }
    }


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        NettyConnection connection = NettyConnectionManager.putIfAbsent(ctx.channel());
        //make sure send success otherwise return an error response
        promise.addListener((f)->{
            if(f.isSuccess()){
                connectionHandler.send(connection,msg);
                return;
            }
            Throwable cause = f.cause();
            if(cause!=null && msg instanceof RpcRequest){
                RpcResponse response = new RpcResponse();
                response.setId(((RpcRequest) msg).getId());
                response.setErrorMsg(cause.getMessage());
                response.setStatus(StatusConstants.BAD_REQUEST);
                connectionHandler.received(connection,response);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try{
            NettyConnection connection = NettyConnectionManager.putIfAbsent(ctx.channel());
            connectionHandler.caught(connection,cause);
        }finally {
            NettyConnectionManager.removeIfDisconnected(ctx.channel());
        }
    }
}
