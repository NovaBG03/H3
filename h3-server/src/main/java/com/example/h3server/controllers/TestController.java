package com.example.h3server.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

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
    public String hello() {
        return "Hello World";
    }
}
