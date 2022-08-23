package io.fxtahe.rpc.common.serialize;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import io.fxtahe.rpc.common.exception.DeSerializeException;
import io.fxtahe.rpc.common.exception.SerializeException;
import io.fxtahe.rpc.common.ext.annotation.Extension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author fxtahe
 * @since 2022/8/19 10:45
 */
@Extension(alias = "hessian")
public class HessianSerialization implements Serialization{

    @Override
    public byte[] serialize(Object obj) throws SerializeException {
        byte[] bytes;
        try( ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
            Hessian2Output hessian2Output = new Hessian2Output(byteArrayOutputStream);
            hessian2Output.writeObject(obj);
            hessian2Output.flush();
            bytes = byteArrayOutputStream.toByteArray();
        }catch (Exception e){
            throw new SerializeException(obj.getClass(),e.getMessage());
        }
        return bytes;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> tClass) throws SerializeException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)){
            Hessian2Input input = new Hessian2Input(byteArrayInputStream);
            return (T) input.readObject();
        } catch (IOException e) {
            throw new DeSerializeException(tClass,e.getMessage());
        }
    }
}
