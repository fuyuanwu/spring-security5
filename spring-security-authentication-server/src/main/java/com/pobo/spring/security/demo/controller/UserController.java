package com.pobo.spring.security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserDetailsManager userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(path = {"/me"}, method = {RequestMethod.GET})
    Principal me(Principal principal) {
        return principal;
    }

    @RequestMapping(path = "", method = {RequestMethod.POST})
    ResponseEntity user(@RequestBody User user) {
        org.springframework.security.core.userdetails.User.UserBuilder userBuilder = org.springframework.security.core.userdetails.User.builder();
        userBuilder.passwordEncoder(passwordEncoder::encode);
        userBuilder.username(user.getName());
        userBuilder.authorities(user.roles.toArray(new String[]{}));
        userBuilder.password(user.getPassword());

        userDetailsService.createUser(userBuilder.build());
        return ResponseEntity.ok().build();
    }


    public static class User {
        private String name;
        private String password;
        private List<String> roles = new ArrayList<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }
}
