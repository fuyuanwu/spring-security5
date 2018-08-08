package com.pobo.spring.security.demo.controller;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
public class LoginController {

    @GetMapping("/user")
    public Principal user(Principal principal) {
        return principal;
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }
}