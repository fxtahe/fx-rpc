package io.fxtahe.rpc.common.serialize;


import io.fxtahe.rpc.common.exception.SerializeException;

/**
 * @author fxtahe
 * @since 2022/8/18 16:10
 */
public interface Serialization {


    /**
     * 序列化对象
     * @param obj
     * @return
     * @throws SerializeException
     */
    byte[] serialize(Object obj) throws SerializeException;

    /**
     * 反序列化对象
     * @param bytes
     * @param tClass
     * @param <T>
     * @return
     * @throws SerializeException
     */
    <T> T deserialize(byte[] bytes,Class<T> tClass) throws SerializeException;

}
