package com.deepthinking.ext.serialize;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;

public class FastJson2RedisSerializer<T> implements RedisSerializer<T> {


    private final Class<T> clazz;

    public FastJson2RedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        try {
            return JSON.toJSONString(t, JSONWriter.Feature.WriteClassName, JSONWriter.Feature.NotWriteRootClassName).getBytes(StandardCharsets.UTF_8);
        } catch (Exception var3) {
            throw new SerializationException("Could not serialize: " + var3.getMessage(), var3);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        try {
            String str = new String(bytes, StandardCharsets.UTF_8);
            return (T) JSON.parseObject(str, clazz, JSONReader.Feature.SupportAutoType);
        } catch (Exception var3) {
            throw new SerializationException("Could not deserialize: " + var3.getMessage(), var3);
        }
    }

}