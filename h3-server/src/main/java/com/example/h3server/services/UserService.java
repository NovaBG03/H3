package com.example.h3server.services;

import com.example.h3server.dtos.user.UserTokenDTO;
import com.example.h3server.exception.CustomException;
import com.example.h3server.models.Role;
import com.example.h3server.models.User;
import com.example.h3server.repositories.UserRepository;
import com.example.h3server.security.JwtTokenProvider;
import com.example.h3server.utils.ModelValidator;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    public UserTokenDTO signIn(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            User user = userRepository.findByUsername(username);

            final String token = jwtTokenProvider
                    .createToken(user.getUsername(), Lists.newArrayList(user.getRoles()));

            return UserTokenDTO.builder()
                    .token(token)
                    .expiresIn(jwtTokenProvider.getExpirationDate(token))
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .roles(user.getRoles())
                    .build();
        } catch (AuthenticationException e) {
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public UserTokenDTO signUp(User user) {
        if (!userRepository.existsByUsername(user.getUsername())) {
            user.setRoles(Sets.newHashSet(Role.ROLE_USER));

            ModelValidator.validate(user);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);

            final String token = jwtTokenProvider
                    .createToken(savedUser.getUsername(), Lists.newArrayList(savedUser.getRoles()));

            return UserTokenDTO.builder()
                    .token(token)
                    .expiresIn(jwtTokenProvider.getExpirationDate(token))
                    .id(savedUser.getId())
                    .username(savedUser.getUsername())
                    .email(savedUser.getEmail())
                    .roles(savedUser.getRoles())
                    .build();
        } else {
            throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public void delete(String username) {
        userRepository.deleteByUsername(username);
    }

    public User search(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
        }
        return user;
    }

    public User whoAmI(HttpServletRequest req) {
        return userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
    }

    public String refresh(String username) {
        return jwtTokenProvider
                .createToken(username, Lists.newArrayList(userRepository.findByUsername(username).getRoles()));
    }

    public void updateProfilePicture(MultipartFile image, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
        }
        
        try {
            byte[] bytes = image.getBytes();
            int bytesLength = bytes.length;

            Byte[] imageBytes = new Byte[bytesLength];
            for (int i = 0; i < bytes.length; i++) {
                imageBytes[i] = bytes[i];
            }

            user.setProfilePicture(imageBytes);
        } catch (IOException e) {
            //todo catch exception
            log.error(e.getMessage());
        }

        userRepository.save(user);
    }

    public byte[] getProfilePicture(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
        }

        Byte[] bytes = user.getProfilePicture();
        if (bytes == null) {
            return null;
        }

        int bytesLength = bytes.length;

        byte[] imageBytes = new byte[bytesLength];
        for (int i = 0; i < bytesLength; i++ ) {
            imageBytes[i] = bytes[i];
        }

        return imageBytes;
    }
}
