package com.example.h3server.models;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean isActive;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_permissions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permissions_id"))
    private Set<Permission> permissions = new HashSet<>();


    @Builder
    public User(Long id, String username, String password, Boolean isActive,
                Set<Role> roles, Set<Permission> permissions) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isActive = isActive;
        if (roles != null) {
            this.roles = roles;
        }
        if (permissions != null) {
            this.permissions = permissions;
        }
    }
}
