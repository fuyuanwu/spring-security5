package com.pobo.spring.security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Component;

@Component
public class RedisClientDetailsService implements ClientDetailsService {
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    private String prefix = "";
    private static final String CLIENT_KEY = "client:";

    @Override
    public ClientDetails loadClientByClientId(final String clientId) throws ClientRegistrationException {
        byte[] serializedClientId = RedisSerialiseUtil.serializeKey(prefix, CLIENT_KEY + clientId);


        byte[] bytes = null;
        RedisConnection conn = getConnection();
        try {
            bytes = conn.get(serializedClientId);
        } finally {
            conn.close();
        }
        final ClientDetails details = RedisSerialiseUtil.deserialize(bytes, ClientDetails.class);

        if (details == null) {
            throw new NoSuchClientException("无法根据client_id查到client信息。client=" + clientId);
        }

        return details;
    }

    public void storeClientDetails(ClientDetails clientDetails) {
        byte[] serializedClientDetails = RedisSerialiseUtil.serialize(clientDetails);
        byte[] serializedClientId = RedisSerialiseUtil.serializeKey(prefix, CLIENT_KEY + clientDetails.getClientId());

        RedisConnection conn = getConnection();
        try {
            // 命令多的话，可以考虑开始管道来批量发送redis命令
            // conn.openPipeline();
            conn.set(serializedClientId, serializedClientDetails);
            // conn.closePipeline();
        } finally {
            conn.close();
        }
    }

    private RedisConnection getConnection() {
        return redisConnectionFactory.getConnection();
    }
}
