package io.fxtahe.rpc.common.remoting;

/**
 * @author fxtahe
 * @since 2022/9/5 10:57
 */
public interface ConnectionHandler {


    void connect(Connection connection);

    void disConnect(Connection connection);

    void send(Connection connection, Object message);

    void received(Connection connection, Object message);

    void caught(Connection connection, Throwable throwable);
}
