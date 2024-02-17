package com.chr.protocol;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

public interface Serializer {
    <T> T deserialize(Class<T> tClass, byte[] bytes);

    <T> byte[] serialize(T t);

    enum Algorithm implements Serializer {
        Java {
            @Override
            public <T> T deserialize(Class<T> tClass, byte[] bytes) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                    return (T) ois.readObject();
                } catch (Exception e) {
                    throw new RuntimeException("反序列化失败", e);
                }
            }

            @Override
            public <T> byte[] serialize(T t) {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(t);
                    return bos.toByteArray();
                } catch (Exception e) {
                    throw new RuntimeException("序列化失败", e);
                }
            }
        },

        Json {
            @Override
            public <T> T deserialize(Class<T> tClass, byte[] bytes) {
                String json = new String(bytes, StandardCharsets.UTF_8);
                return new Gson().fromJson(json, tClass);
            }

            @Override
            public <T> byte[] serialize(T t) {
                String json = new Gson().toJson(t);
                return json.getBytes(StandardCharsets.UTF_8);
            }
        }
    }
}
