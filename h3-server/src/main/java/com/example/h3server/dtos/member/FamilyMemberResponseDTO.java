package com.example.h3server.dtos.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMemberResponseDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private LocalDate birthday;

    private LocalDate dateOfDeath;

    private Long fatherId;

    private Long motherId;
}
