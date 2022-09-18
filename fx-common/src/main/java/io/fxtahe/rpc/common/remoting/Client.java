package io.fxtahe.rpc.common.remoting;

import io.fxtahe.rpc.common.exception.RemotingException;

/**
 * @author fxtahe
 * @since 2022/9/5 10:25
 */
public interface Client {

    void connect();

    void reConnect();

    void send(Object message);

    void disConnect();

    boolean isConnected();

    void close();

    boolean isClosed();

}
