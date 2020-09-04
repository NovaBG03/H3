package com.example.h3server.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3, max = 225, message = "Username must be from 3 to 225 symbols")
    @Column(unique = true, nullable = false)
    private String username;

    @Email(message = "Invalid email address.")
    @Size(min = 3, max = 225, message = "Email must be from 3 to 225 symbols")
    @Column(unique = false, nullable = false)
    private String email;

    @Size(min = 6, max = 225, message = "Password must be from 6 to 225 symbols")
    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(value = EnumType.STRING)
    private List<Role> roles = new ArrayList<>();

    @Builder
    public User(Long id, String username, String email, String password, List<Role> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        if (roles != null) {
            this.roles = roles;
        }
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }
}
