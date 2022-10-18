package io.fxtahe.rpc.common.serialize;

import io.fxtahe.rpc.common.exception.SerializeException;
import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.util.ProtostuffUtil;

/**
 * @author fxtahe
 * @since 2022/10/18 9:49
 */
@Extension(alias = "protobuf")
public class ProtobufSerialization implements Serialization {


    @Override
    public byte[] serialize(Object obj) throws SerializeException {
        return ProtostuffUtil.serialize(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> tClass) throws SerializeException {
        return ProtostuffUtil.deserialize(bytes, tClass);
    }

}
