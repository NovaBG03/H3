package com.example.h3server.bootstrap;

import com.example.h3server.models.Permission;
import com.example.h3server.models.Role;
import com.example.h3server.models.User;
import com.example.h3server.repositories.PermissionRepository;
import com.example.h3server.repositories.RoleRepository;
import com.example.h3server.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class Bootstrap implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    public Bootstrap(PasswordEncoder passwordEncoder, RoleRepository roleRepository,
                     PermissionRepository permissionRepository,
                     UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        this.loadRoles();
        this.loadPermissions();
        this.loadUsers();
    }

    private void loadUsers() {
        Set<Permission> rootPermissions = new HashSet<>();
        rootPermissions.add(permissionRepository.findByName("useless"));

        Set<Role> rootRoles = new HashSet<>();
        rootRoles.add(roleRepository.findByName("ROLE_USER"));
        rootRoles.add(roleRepository.findByName("ROLE_ADMIN"));

        User root = User.builder()
                .username("root")
                .password(this.passwordEncoder.encode("root"))
                .isActive(true)
                .permissions(rootPermissions)
                .roles(rootRoles)
                .build();
        this.userRepository.save(root);


        Set<Role> userRoles = new HashSet<>();
        userRoles.add(roleRepository.findByName("ROLE_USER"));

        User user = User.builder()
                .username("user")
                .password(this.passwordEncoder.encode("user"))
                .isActive(true)
                .roles(userRoles)
                .build();
        this.userRepository.save(user);
    }

    private void loadPermissions() {
        Permission useless = Permission.builder().name("USELESS").build();
        this.permissionRepository.save(useless);
    }

    private void loadRoles() {
        Role user = Role.builder().name("ROLE_USER").build();
        this.roleRepository.save(user);

        Role admin = Role.builder().name("ROLE_ADMIN").build();
        this.roleRepository.save(admin);
    }
}
