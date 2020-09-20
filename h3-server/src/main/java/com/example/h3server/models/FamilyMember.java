package com.example.h3server.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FamilyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "First name must be from 3 to 225 symbols")
    @Size(min = 3, max = 225, message = "First name must be from 3 to 225 symbols")
    @Column(nullable = false)
    private String firstName;

    @NotNull(message = "Last name must be from 3 to 225 symbols")
    @Size(min = 3, max = 225, message = "Last name must be from 3 to 225 symbols")
    @Column(nullable = false)
    private String lastName;

    private LocalDate birthday;

    // TODO check that dateOfDeath is after birthday before saving
    private LocalDate dateOfDeath;

    @NotNull(message = "Something went wrong")
    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.UNKNOWN;

    @OneToOne
    @JoinColumn(name = "father_id")
    private FamilyMember father;

    @OneToOne
    @JoinColumn(name = "mother_id")
    private FamilyMember mother;

    @ManyToOne
    private FamilyTree familyTree;

    @Builder
    public FamilyMember(Long id,
                        String firstName,
                        String lastName,
                        LocalDate birthday,
                        LocalDate dateOfDeath,
                        FamilyMember father,
                        FamilyMember mother,
                        FamilyTree familyTree,
                        Gender gender) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.dateOfDeath = dateOfDeath;
        this.father = father;
        this.mother = mother;
        this.familyTree = familyTree;
        if (gender != null) {
            this.gender = gender;
        }
    }
}
