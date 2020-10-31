package com.example.h3server.models;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Username must be from 3 to 225 symbols")
    @Size(min = 3, max = 225, message = "Username must be from 3 to 225 symbols")
    @Column(unique = true, nullable = false)
    private String username;

    @NotNull(message = "Email must be valid and from 3 to 225 symbols")
    @Pattern(regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", message = "Email must be valid and from 3 to 225 symbols")
    @Size(min = 3, max = 225, message = "Email must be valid and from 3 to 225 symbols")
    @Column(unique = false, nullable = false)
    private String email;

    @NotNull(message = "Password must be from 6 to 225 symbols")
    @Size(min = 6, max = 225, message = "Password must be from 6 to 225 symbols")
    @Column(nullable = false)
    private String password;

    @Lob
    private Byte[] profilePicture;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(value = EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<FamilyTree> familyTrees = new HashSet<>();

    @Builder
    public User(Long id,
                String username,
                String email,
                String password,
                Byte[] profilePicture,
                Set<Role> roles,
                Set<FamilyTree> familyTrees) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.profilePicture = profilePicture;
        if (roles != null) {
            this.roles = roles;
        }
        if (familyTrees != null) {
            this.familyTrees = familyTrees;
        }
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void addFamilyTree(FamilyTree familyTree) {
        this.familyTrees.add(familyTree);
        familyTree.setUser(this);
    }

    public Boolean isAdmin() {
        return this.roles.contains(Role.ROLE_ADMIN);
    }
}
