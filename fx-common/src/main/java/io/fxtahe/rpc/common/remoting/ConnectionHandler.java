package io.fxtahe.rpc.common.remoting;

/**
 * @author fxtahe
 * @since 2022/9/5 10:57
 */
public interface ConnectionHandler {


    default void connect(Connection connection){

    }

    default void disConnect(Connection connection){

    }

    default void send(Connection connection, Object message){

    }

    default void received(Connection connection, Object message){

    }

    default void caught(Connection connection, Throwable throwable){

    }
}
