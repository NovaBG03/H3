package com.example.h3server.mappers;

import com.example.h3server.dtos.member.FamilyMemberDataDTO;
import com.example.h3server.dtos.member.FamilyMemberResponseDTO;
import com.example.h3server.models.FamilyMember;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith({MockitoExtension.class})
class FamilyMemberMapperTest {

    private Long id = 1L;
    private String firstName = "first";
    private String lastName = "last";
    private LocalDate birthday = LocalDate.of(2020, 9, 11);
    private LocalDate dateOfDeath = null;
    private FamilyMember father = null;

    private Long motherId;
    private FamilyMember mother = FamilyMember.builder()
            .id(motherId)
            .build();

    @Test
    void familyMemberToFamilyMemberResponseDTO() {
        FamilyMember familyMember = FamilyMember.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .birthday(birthday)
                .dateOfDeath(dateOfDeath)
                .father(father)
                .mother(mother)
                .build();

        FamilyMemberResponseDTO familyMemberResponseDTO = FamilyMemberMapper.INSTANCE
                .familyMemberToFamilyMemberResponseDTO(familyMember);

        assertEquals(familyMember.getId(), familyMemberResponseDTO.getId());
        assertEquals(familyMember.getFirstName(), familyMemberResponseDTO.getFirstName());
        assertEquals(familyMember.getLastName(), familyMemberResponseDTO.getLastName());
        assertEquals(familyMember.getBirthday(), familyMemberResponseDTO.getBirthday());
        assertEquals(familyMember.getDateOfDeath(), familyMemberResponseDTO.getDateOfDeath());
        assertEquals(familyMember.getMother().getId(), familyMemberResponseDTO.getMotherId());
        assertNull(familyMemberResponseDTO.getFatherId());
    }

    @Test
    void familyMemberDataDTOToFamilyMember() {
        FamilyMemberDataDTO familyMemberDataDTO =
                new FamilyMemberDataDTO(firstName, lastName, birthday, dateOfDeath, null, motherId);

        FamilyMember familyMember = FamilyMemberMapper.INSTANCE
                .FamilyMemberDataDTOToFamilyMember(familyMemberDataDTO);

        assertNull(familyMember.getId());
        assertEquals(familyMemberDataDTO.getFirstName(), familyMember.getFirstName());
        assertEquals(familyMemberDataDTO.getLastName(), familyMember.getLastName());
        assertEquals(familyMemberDataDTO.getBirthday(), familyMember.getBirthday());
        assertEquals(familyMemberDataDTO.getDateOfDeath(), familyMember.getDateOfDeath());
        assertEquals(familyMemberDataDTO.getFatherId(), familyMember.getFather().getId());
        assertEquals(familyMemberDataDTO.getMotherId(), familyMember.getMother().getId());
        assertNull(familyMember.getFamilyTree());
    }
}