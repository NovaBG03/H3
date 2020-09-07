package com.example.h3server.controllers;

import com.example.h3server.dtos.user.UserDataDTO;
import com.example.h3server.dtos.user.UserResponseDTO;
import com.example.h3server.dtos.user.UserTokenDTO;
import com.example.h3server.mappers.UserMapper;
import com.example.h3server.services.UserService;
import io.swagger.annotations.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/users")
@Api(tags = "users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signIn")
    @ApiOperation(value = "${UserController.signIn}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 422, message = "Invalid username/password supplied")})
    public UserTokenDTO login(@ApiParam("Username") @RequestParam String username,
                        @ApiParam("Password") @RequestParam String password) {
        return userService.signIn(username, password);
    }

    @PostMapping("/signUp")
    @ApiOperation(value = "${UserController.signUp}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong / " +
                    "Username must be from 3 to 225 symbols / " +
                    "Email must be valid and from 3 to 225 symbols / " +
                    "Password must be from 6 to 225 symbols"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 422, message = "Username is already in use"),})
    public UserTokenDTO signUp(@ApiParam("SignUp User") @RequestBody UserDataDTO userDataDTO) {
        return userService.signUp(UserMapper.INSTANCE.userDataDTOToUser(userDataDTO));
    }

    @DeleteMapping(value = "/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // allows only admins to delete users
    @ApiOperation(value = "${UserController.delete}", authorizations = { @Authorization(value="apiKey") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The user doesn't exist"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public String delete(@ApiParam("Username") @PathVariable String username) {
        userService.delete(username);
        return username;
    }

    @GetMapping(value = "/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // allows only admins to search users
    @ApiOperation(value = "${UserController.search}",
            response = UserResponseDTO.class,
            authorizations = { @Authorization(value="apiKey") })
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The user doesn't exist"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public UserResponseDTO search(@ApiParam("Username") @PathVariable String username) {
        return UserMapper.INSTANCE.userToUserResponseDTO(userService.search(username));
    }

    @GetMapping(value = "/me")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiOperation(value = "${UserController.me}",
            response = UserResponseDTO.class,
            authorizations = { @Authorization(value="apiKey") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The user doesn't exist"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public UserResponseDTO whoAmI(HttpServletRequest req) {
        return UserMapper.INSTANCE.userToUserResponseDTO(userService.whoAmI(req));
    }

    @GetMapping("/refresh")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public String refresh(HttpServletRequest req) {
        return userService.refresh(req.getRemoteUser());
    }
}
