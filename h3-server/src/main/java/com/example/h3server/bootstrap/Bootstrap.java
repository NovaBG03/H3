package com.example.h3server.bootstrap;

import com.example.h3server.models.Role;
import com.example.h3server.models.User;
import com.example.h3server.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Bootstrap implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public Bootstrap(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        this.loadUsers();
    }

    private void loadUsers() {

        User root = User.builder()
                .username("root")
                .email("root@root.com")
                .password(this.passwordEncoder.encode("root"))
                .build();
        root.addRole(Role.ROLE_ADMIN);
        root.addRole(Role.ROLE_USER);
        this.userRepository.save(root);

        User user = User.builder()
                .username("user")
                .email("user@user.com")
                .password(this.passwordEncoder.encode("user"))
                .build();
        user.addRole(Role.ROLE_USER);
        this.userRepository.save(user);

        log.info("Loaded Users: " + this.userRepository.count());
    }
}
