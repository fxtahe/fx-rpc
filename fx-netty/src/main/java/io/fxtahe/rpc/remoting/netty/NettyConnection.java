package io.fxtahe.rpc.remoting.netty;

import io.fxtahe.rpc.common.remoting.Connection;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fxtahe
 * @since 2022/9/5 11:04
 */
public class NettyConnection implements Connection {

    /**
     * netty channel
     */
    private Channel channel;

    private Map<String,Object> attributes = new ConcurrentHashMap<>();


    public NettyConnection(Channel channel) {
        this.channel = channel;
    }

    @Override
    public boolean isClosed() {
        return !channel.isActive();
    }

    @Override
    public void send(Object message) {
        channel.writeAndFlush(message);
    }

    @Override
    public void close() {

        channel.close();
    }

    @Override
    public void setAttribute(String key, Object value) {
        if(value==null){
            attributes.remove(key);
        }else {
            attributes.put(key,value);
        }
    }

    @Override
    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }
}
