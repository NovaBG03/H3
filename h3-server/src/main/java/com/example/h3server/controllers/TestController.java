package com.example.h3server.controllers;

import com.example.h3server.services.MyUserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class TestController {

    private final MyUserDetailsService myUserDetailsService;

    public TestController(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    @GetMapping()
    public String base() {
        return "<h1>Base</h1p>";
    }

    @GetMapping("user")
    public String user() {
        return "<h1>User</h1p>";
    }

    @GetMapping("admin")
    public String admin() {
        return "<h1>Admin</h1p>";
    }

    @GetMapping("/hello")
    public String hello(Principal principal) {
        return principal.getName();
    }
}
