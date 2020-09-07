package com.example.h3server.services;

import com.example.h3server.dtos.user.UserTokenDTO;
import com.example.h3server.exception.CustomException;
import com.example.h3server.models.Role;
import com.example.h3server.models.User;
import com.example.h3server.repositories.UserRepository;
import com.example.h3server.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    AuthenticationManager authenticationManager;

    @InjectMocks
    UserService userService;

    Long id = 1L;
    String email = "email@abv.bg";
    String username = "username";
    String password = "password";
    String encodedPassword = "encoded";
    String token = "token";
    Date expiresIn = new Date();
    User user;
    List<Role> roles;

    @BeforeEach
    void setUp() {
        roles = Arrays.asList(Role.ROLE_USER);
        user = User.builder()
                .id(id)
                .email(email)
                .username(username)
                .password(password)
                .roles(roles)
                .build();
    }

    @Test
    void signInSuccessfully() {
        // given
        given(userRepository.findByUsername(username)).willReturn(user);
        given(jwtTokenProvider.createToken(username, roles)).willReturn(token);
        given(jwtTokenProvider.getExpirationDate(token)).willReturn(expiresIn);

        // when
        UserTokenDTO userTokenDTO = userService.signIn(username, password);

        // then
        assertEquals(id, userTokenDTO.getId());
        assertEquals(token, userTokenDTO.getToken());
        assertEquals(username, userTokenDTO.getUsername());
        assertEquals(email, userTokenDTO.getEmail());
        assertEquals(expiresIn, userTokenDTO.getExpiresIn());
        assertEquals(roles, userTokenDTO.getRoles());
    }

    @Test
    void signInInvalidCredentials() {
        // given
        given(authenticationManager.authenticate(any())).willThrow(UsernameNotFoundException.class);

        // when
        // then
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.signIn(username, password));

        assertEquals("Invalid username/password supplied", exception.getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getHttpStatus());
    }

    @Test
    void signUpSuccessfully() {
        // given
        User savedUser = User.builder()
                .id(id)
                .username(username)
                .password(encodedPassword)
                .email(email)
                .roles(roles)
                .build();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        given(userRepository.existsByUsername(username)).willReturn(false);
        given(passwordEncoder.encode(password)).willReturn(encodedPassword);
        given(userRepository.save(userCaptor.capture())).willReturn(savedUser);
        given(jwtTokenProvider.createToken(username, roles)).willReturn(token);
        given(jwtTokenProvider.getExpirationDate(token)).willReturn(expiresIn);

        // when
        UserTokenDTO userTokenDTO = userService.signUp(user);

        // then
        User capturedUser = userCaptor.getValue();
        assertEquals(encodedPassword, capturedUser.getPassword());
        assertEquals(Arrays.asList(Role.ROLE_USER), capturedUser.getRoles());

        assertEquals(id, userTokenDTO.getId());
        assertEquals(token, userTokenDTO.getToken());
        assertEquals(username, userTokenDTO.getUsername());
        assertEquals(email, userTokenDTO.getEmail());
        assertEquals(expiresIn, userTokenDTO.getExpiresIn());
        assertEquals(roles, userTokenDTO.getRoles());
    }

    @Test
    void signUpInvalidCredentials() {
        // given
        given(userRepository.existsByUsername(username)).willReturn(true);

        // when
        //then
        CustomException exception = assertThrows(CustomException.class, () -> userService.signUp(user));
        assertEquals("Username is already in use", exception.getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getHttpStatus());
    }

    @Test
    void delete() {
        // given
        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);

        // when
        userService.delete(username);

        // then
        verify(userRepository, times(1)).deleteByUsername(usernameCaptor.capture());
        assertEquals(username, usernameCaptor.getValue());
    }

    @Test
    void searchUserExists() {
        // given
        given(userRepository.findByUsername(username)).willReturn(user);

        // when
        User user = userService.search(username);

        // then
        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(roles, user.getRoles());
    }

    @Test
    void searchUserNotFound() {
        // given
        given(userRepository.findByUsername(username)).willReturn(null);

        // when
        // then
        CustomException exception = assertThrows(CustomException.class, () -> userService.search(username));
        assertEquals("The user doesn't exist", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    void whoAmI() {
        // given
        HttpServletRequest req = new MockHttpServletRequest();
        given(jwtTokenProvider.resolveToken(req)).willReturn(token);
        given(jwtTokenProvider.getUsername(token)).willReturn(username);
        given(userRepository.findByUsername(username)).willReturn(user);

        // when
        User user = userService.whoAmI(req);

        // then
        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(roles, user.getRoles());
    }

    @Test
    void refresh() {
        // given
        given(userRepository.findByUsername(username)).willReturn(user);
        given(jwtTokenProvider.createToken(username, roles)).willReturn(token);

        // when
        String refreshedToken = userService.refresh(username);

        // then
        assertEquals(token, refreshedToken);
    }
}