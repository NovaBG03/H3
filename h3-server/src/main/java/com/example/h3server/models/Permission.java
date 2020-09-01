package com.example.h3server.models;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "permissions")
    private Set<User> users = new HashSet<>();

    @Builder
    public Permission(Long id, String name, Set<User> users) {
        this.id = id;
        this.name = name;
        if (users != null) {
            this.users = users;
        }
    }
}
