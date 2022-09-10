package io.fxtahe.rpc.common.remoting;

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

}
