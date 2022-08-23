package io.fxtahe.rpc.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fxtahe.rpc.common.exception.DeSerializeException;
import io.fxtahe.rpc.common.exception.SerializeException;

import java.io.IOException;

/**
 * @author fxtahe
 * @since 2022/8/23 9:51
 */
public final class JsonUtil {

    static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


    public static <T> T readJsonString(String json, Class<T> clazz){
        try {
            return mapper.readValue(json,clazz);
        } catch (IOException e) {
            throw new DeSerializeException(clazz,e.getMessage());
        }
    }

    public static <T> T readJsonBytes(byte[] json, Class<T> clazz){
        try {
            return mapper.readValue(json,clazz);
        } catch (IOException e) {
            throw new DeSerializeException(clazz,e.getMessage());
        }
    }

    public static String writeJson(Object obj){
        try {
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new SerializeException(obj.getClass(),e.getMessage());
        }
    }


    public static byte[] writeJsonAsBytes(Object obj){
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (IOException e) {
            throw new SerializeException(obj.getClass(),e.getMessage());
        }
    }



}
