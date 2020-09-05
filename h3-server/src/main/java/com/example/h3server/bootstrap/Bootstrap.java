package com.example.h3server.bootstrap;

import com.example.h3server.models.FamilyTree;
import com.example.h3server.models.Role;
import com.example.h3server.models.User;
import com.example.h3server.repositories.FamilyTreeRepository;
import com.example.h3server.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class Bootstrap implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final FamilyTreeRepository familyTreeRepository;

    public Bootstrap(PasswordEncoder passwordEncoder,
                     UserRepository userRepository,
                     FamilyTreeRepository familyTreeRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.familyTreeRepository = familyTreeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        this.loadUsers();
        this.loadFamilyTrees();
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

    private void loadFamilyTrees() {
        this.familyTreeRepository.save(FamilyTree.builder()
                .name("My First Family Tree")
                .isPrivate(true)
                .createdAt(LocalDateTime.now())
                .user(userRepository.findByUsername("root"))
                .build());

        FamilyTree queensFamilyTree = this.familyTreeRepository.save(FamilyTree.builder()
                .name("Queen's Family Tree")
                .isPrivate(false)
                .createdAt(LocalDateTime.now())
                .user(userRepository.findByUsername("root"))
                .build());

        log.info("Loaded Family Trees: " + this.familyTreeRepository.count());

        // for testing, TODO remove
        // this.familyTreeRepository.delete(queensFamilyTree);
    }

}
