package com.pobo.spring.security.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/user")
public class UserController {
    @RequestMapping(path = {"/", ""}, method = {RequestMethod.GET, RequestMethod.POST})
    public Principal user(Principal principal) {
        return principal;
    }
}
