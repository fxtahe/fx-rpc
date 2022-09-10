package io.fxtahe.rpc.remoing.netty;

import io.netty.channel.Channel;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * manage channel
 * @author fxtahe
 * @since 2022/9/5 16:25
 */
public class NettyConnectionManager {


    private static final Map<Channel,NettyConnection> CONNECTION_CACHE = new ConcurrentHashMap<>();


    public static NettyConnection putIfAbsent(Channel channel){
        NettyConnection connection = null;
        if(channel!=null && channel.isActive()){
             connection = CONNECTION_CACHE.putIfAbsent(channel, new NettyConnection(channel));
            if(connection.isClosed()){
                CONNECTION_CACHE.remove(channel);
                connection=null;
            }
        }
        return connection;
    }


    public static void removeIfDisconnected(Channel channel){
        if(channel!=null && !channel.isActive()){
            CONNECTION_CACHE.remove(channel);
        }
    }


    public static Collection<NettyConnection> getConnections(){

        return CONNECTION_CACHE.values();

    }



}
