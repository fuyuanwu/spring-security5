package com.pobo.spring.security.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.util.Assert;


public class RedisUserDetailsService implements UserDetailsManager {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private RedisConnectionFactory redisConnectionFactory;
    private PasswordEncoder passwordEncoder;

    private static final String USERNAME_KEY = "username:";
    private AuthenticationManager authenticationManager;

    public void createUser(UserDetails user) {
        Assert.isTrue(!userExists(user.getUsername()), "user should not exist");
        saveOrUpdate(user);
    }


    private void saveOrUpdate(UserDetails user) {
        RedisConnection conn = getConnection();
        try {
            byte[] key = RedisSerialiseUtil.serializeKey(USERNAME_KEY, user.getUsername());
            byte[] value = RedisSerialiseUtil.serialize(user);
            conn.set(key, value);
        } finally {
            conn.close();
        }
    }

    public void deleteUser(String username) {
        RedisConnection conn = getConnection();

        try {
            byte[] key = RedisSerialiseUtil.serializeKey(USERNAME_KEY, username);
            Long count = conn.del(key);
            logger.info("deleteUser {} is {}", username, Boolean.valueOf(count > 0).toString());
        } finally {
            conn.close();
        }
    }

    public void updateUser(UserDetails user) {
        Assert.isTrue(userExists(user.getUsername()), "user should exist");
        saveOrUpdate(user);
    }

    public boolean userExists(String username) {
        return loadUserByUsername(username) != null;
    }

    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException("Can't change password as no Authentication object found in context for current user.");
        }

        String username = currentUser.getName();

        logger.debug("Changing password for user '" + username + "'");

        // If an authentication manager has been set, re-authenticate the user with the
        // supplied password.
        if (authenticationManager != null) {
            logger.debug("Reauthenticating user '" + username + "' for password change request.");

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));
        } else {
            logger.debug("No authentication manager set. Password won't be re-checked.");
        }


        RedisConnection conn = getConnection();

        try {
            UserDetails userDetails = loadUserByUsername(username);

//            // 一些验证
//            if (!userDetails.isAccountNonExpired()) {
//                throw new AccessDeniedException("账户已失效");
//            }
//
//            if (!userDetails.isAccountNonLocked()) {
//                throw new AccessDeniedException("账户已失效");
//            }
//
//            if (oldPassword != null && oldPassword.equals(newPassword)) {
//                throw new AccessDeniedException("新老密码不能一样");
//            }

            User.UserBuilder userBuilder = User.withUserDetails(userDetails).password(newPassword);
            userBuilder.passwordEncoder(passwordEncoder::encode);

            saveOrUpdate(userBuilder.build());
        } finally {
            conn.close();
        }
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        RedisConnection conn = getConnection();
        try {
            byte[] key = RedisSerialiseUtil.serializeKey(USERNAME_KEY, username);
            byte[] value = conn.get(key);
            return RedisSerialiseUtil.deserialize(value, UserDetails.class);
        } finally {
            conn.close();
        }
    }

    private RedisConnection getConnection() {
        return redisConnectionFactory.getConnection();
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setRedisConnectionFactory(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }
}
