package com.pobo.spring.security.demo.controller;

import com.pobo.spring.security.demo.dto.ClientDetailsDto;
import com.pobo.spring.security.demo.service.RedisClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/client")
@RestController
public class ClientController {
    @Autowired
    RedisClientDetailsService redisClientDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping(value = {"", "/"})
    ResponseEntity addClient(@RequestBody BaseClientDetails clientDetails) {
        clientDetails.setClientSecret(passwordEncoder.encode(clientDetails.getClientSecret()));
        redisClientDetailsService.storeClientDetails(clientDetails);
        return ResponseEntity.ok().build();
    }
}
