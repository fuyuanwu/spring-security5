package com.pobo.spring.security.demo.service;

import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;

public class RedisSerialiseUtil {
    private static RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();

    public static byte[] serialize(Object object) {
        return serializationStrategy.serialize(object);
    }

    public static byte[] serializeKey(String prefix, String object) {
        return serialize(prefix + object);
    }

    public static<T> T deserialize(byte[] bytes, Class<T> t) {
        return serializationStrategy.deserialize(bytes, t);
    }

    public static byte[] serialize(String string) {
        return serializationStrategy.serialize(string);
    }

    public static String deserializeString(byte[] bytes) {
        return serializationStrategy.deserializeString(bytes);
    }
}
