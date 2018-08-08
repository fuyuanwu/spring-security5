package com.pobo.spring.security.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestEndpoints {
    @GetMapping("/public/{id}")
    public String getProduct(@PathVariable String id) {
        return "public id : " + id;
    }

    @GetMapping("/protect/{id}")
    public String getOrder(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return "protect id: " + id + ", name: " + authentication.getName();
    }

}