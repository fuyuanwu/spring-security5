package com.pobo.spring.security.demo.configuration;

import com.pobo.spring.security.demo.service.RedisClientDetailsServiceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
    @Autowired
    RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private RedisClientDetailsServiceBuilder redisClientDetailsServiceBuilder;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // 使用自己的redis来存放client的认证数据
        clients.setBuilder(redisClientDetailsServiceBuilder);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients(); // 允许from表单提交
        security.checkTokenAccess("authenticated"); // 具有什么权限的才会反问接口
        security.tokenKeyAccess("authenticated");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setPrefix("oauth2_");
        endpoints.tokenStore(redisTokenStore);
        endpoints.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    }
}