package com.example.h3server.controllers;

import com.example.h3server.dtos.MessageDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
@Api(tags = "public")
public class PublicController {

    @GetMapping("/ping")
    @ApiOperation(value = "${PublicController.ping}")
    public MessageDTO ping() {
        return new MessageDTO("Pong!");
    }
}
