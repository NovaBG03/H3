package com.example.h3server.models;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Fact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Fact Name must be from 3 to 30 symbols")
    @Size(min = 3, max = 30, message = "Fact Name must be from 3 to 30 symbols")
    private String name;

    @Lob
    private Byte[] image;

    @Size(min = 3, max = 255, message = "Fact Description must be from 3 to 255 symbols")
    private String description;

    @ManyToOne
    private FamilyTree familyTree;

    @ManyToOne
    private FamilyMember familyMember;

    @Builder
    public Fact(Long id,
                String name,
                Byte[] image,
                String description,
                FamilyTree familyTree,
                FamilyMember familyMember) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.description = description;
        this.familyTree = familyTree;
        this.familyMember = familyMember;
    }
}
