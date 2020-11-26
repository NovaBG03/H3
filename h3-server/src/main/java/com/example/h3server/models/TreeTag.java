package com.example.h3server.models;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TreeTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "TreeTag label must be from 3 to 30 symbols")
    @Size(min = 3, max = 10, message = "TreeTag label must be from 3 to 30 symbols")
    @Column(nullable = false, unique = true)
    private String label;

    @ManyToMany(mappedBy = "tags")
    private Set<FamilyTree> familyTrees = new HashSet<>();

    @Builder
    public TreeTag(Long id, String label, Set<FamilyTree> familyTrees) {
        this.id = id;
        this.label = label;
        if (familyTrees != null) {
            this.familyTrees = familyTrees;
        }
    }
}
