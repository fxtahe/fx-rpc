package io.fxtahe.rpc.common.serialize;

import io.fxtahe.rpc.common.ext.ExtensionLoaderFactory;

/**
 * @author fxtahe
 * @since 2022/9/9 17:39
 */
public class SerializationFactory {



    public static Serialization buildSerialization(SerializationEnum serializationEnum){

        return ExtensionLoaderFactory.getExtensionLoader(Serialization.class).getInstance(serializationEnum.getName());
    }


}
