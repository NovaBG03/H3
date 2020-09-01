package com.example.h3server.controllers;

import com.example.h3server.dtos.authentication.AuthenticationRequest;
import com.example.h3server.dtos.authentication.AuthenticationResponse;
import com.example.h3server.services.MyUserDetailsService;
import com.example.h3server.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService myUserDetailsService;
    private final JwtUtil jwtUtil;

    public AuthenticationController(AuthenticationManager authenticationManager,
                                    MyUserDetailsService myUserDetailsService,
                                    JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.myUserDetailsService = myUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/authenticate")
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)
            throws Exception {
        final String username = authenticationRequest.getUsername();
        final String password = authenticationRequest.getPassword();

        try {
            this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
        }
        catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = this.myUserDetailsService.loadUserByUsername(username);

        final String jwt = this.jwtUtil.generateToken(userDetails);

        return new AuthenticationResponse(jwt);
    }

}
