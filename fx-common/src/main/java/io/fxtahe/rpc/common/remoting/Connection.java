package io.fxtahe.rpc.common.remoting;

/**
 * @author fxtahe
 * @since 2022/9/5 10:57
 */
public interface Connection {


    boolean isClosed();

    void send(Object message);

    void close();

    void setAttribute(String key, Object value);

    void removeAttribute(String key);

    Object getAttribute(String key);

}
