package com.example.h3server.services;

import com.example.h3server.dtos.authentication.RegisterUserDto;
import com.example.h3server.models.MyUserDetails;
import com.example.h3server.models.User;
import com.example.h3server.repositories.RoleRepository;
import com.example.h3server.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public UserDetails registerUser(RegisterUserDto registerUserDto) {
        Optional<User> userOptional = this.getUser(registerUserDto.getUsername());

        if (userOptional.isPresent()) {
            // TODO handle this more elegant
            throw new IllegalArgumentException(
                    "User with username " + registerUserDto.getUsername() + " already exists");
        }

        // map userDto to userEntity
        User user = User.builder()
                .username(registerUserDto.getUsername())
                .password(this.passwordEncoder.encode(registerUserDto.getPassword()))
                .build();

        user.addRole(roleRepository.findByName("ROLE_USER"));

        User savedUser = userRepository.save(user);

        return new MyUserDetails(savedUser);
    }

    public Optional<User> getUser(String username) {
        return userRepository.findByUsername(username);
    }
}
