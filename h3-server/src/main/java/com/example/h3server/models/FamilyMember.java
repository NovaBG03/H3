package com.example.h3server.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FamilyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private LocalDate birthday;

    private LocalDate dateOfDeath;

    @OneToOne
    @JoinColumn(name = "father_id")
    private FamilyMember father;

    @OneToOne
    @JoinColumn(name = "mother_id")
    private FamilyMember mother;

    @ManyToOne
    private FamilyTree familyTree;
}
