package io.fxtahe.rpc.common.serialize;

import io.fxtahe.rpc.common.exception.SerializeException;
import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.util.JsonUtil;

/**
 * @author fxtahe
 * @since 2022/8/23 10:05
 */
@Extension(alias = "json")
public class JsonSerialization implements Serialization{

    @Override
    public byte[] serialize(Object obj) throws SerializeException {
        return JsonUtil.writeJsonAsBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> tClass) throws SerializeException {
        return JsonUtil.readJsonBytes(bytes,tClass);
    }
}
