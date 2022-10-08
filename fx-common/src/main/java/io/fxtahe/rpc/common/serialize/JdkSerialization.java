package io.fxtahe.rpc.common.serialize;

import io.fxtahe.rpc.common.exception.DeSerializeException;
import io.fxtahe.rpc.common.exception.SerializeException;
import io.fxtahe.rpc.common.ext.annotation.Extension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author fxtahe
 * @since 2022/10/8 10:18
 */
@Extension(alias = "java")
public class JdkSerialization implements Serialization {
    @Override
    public byte[] serialize(Object obj) throws SerializeException {
        ByteArrayOutputStream byteArrayOutputStream;
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream = new ByteArrayOutputStream())){
            objectOutputStream.writeObject(obj);
        }catch (IOException e){
            throw new SerializeException(obj.getClass(),e.getMessage());
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> tClass) throws SerializeException {

        T t;
        try(ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes))){
            t = (T) objectInputStream.readObject();
        }catch (IOException | ClassNotFoundException e){
            throw new DeSerializeException(tClass,e.getMessage());
        }
        return t;
    }

}
