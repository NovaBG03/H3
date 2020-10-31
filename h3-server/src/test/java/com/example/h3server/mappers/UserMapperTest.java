package com.example.h3server.mappers;

import com.example.h3server.dtos.user.UserDataDTO;
import com.example.h3server.dtos.user.UserResponseDTO;
import com.example.h3server.models.Role;
import com.example.h3server.models.User;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private Long id = 1L;
    private String username = "username";
    private String email = "email";
    private String password = "password";
    private Set<Role> roles = Sets.newHashSet(Role.ROLE_USER, Role.ROLE_ADMIN);

    @Test
    void userToUserResponseDTO() {
        User user = User.builder()
                .id(id)
                .username(username)
                .email(email)
                .password(password)
                .roles(roles)
                .build();

        UserResponseDTO userResponseDTO = UserMapper.INSTANCE.userToUserResponseDTO(user);

        assertEquals(id, userResponseDTO.getId());
        assertEquals(username, userResponseDTO.getUsername());
        assertEquals(email, userResponseDTO.getEmail());
        assertEquals(roles, userResponseDTO.getRoles());
    }

    @Test
    void userDataDTOToUser() {
        UserDataDTO userDataDTO = new UserDataDTO(username, email, password);

        User user = UserMapper.INSTANCE.userDataDTOToUser(userDataDTO);

        assertNull(user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(0, user.getRoles().size());
        assertEquals(0, user.getFamilyTrees().size());

    }
}