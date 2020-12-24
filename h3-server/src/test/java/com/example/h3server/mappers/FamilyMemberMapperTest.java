//package com.example.h3server.mappers;
//
//import com.example.h3server.dtos.member.FamilyMemberDataDTO;
//import com.example.h3server.dtos.member.FamilyMemberResponseDTO;
//import com.example.h3server.models.FamilyMember;
//import com.example.h3server.models.Gender;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNull;
//
//@ExtendWith({MockitoExtension.class})
//class FamilyMemberMapperTest {
//
//    private Long id = 1L;
//    private String firstName = "first";
//    private String lastName = "last";
//    private LocalDate birthday = LocalDate.of(2020, 9, 11);
//    private LocalDate dateOfDeath = null;
//    private FamilyMember father = null;
//
//    private Long motherId = 5L;
//    private FamilyMember mother = FamilyMember.builder()
//            .id(motherId)
//            .build();
//
//    @Test
//    void familyMemberToFamilyMemberResponseDTO() {
//        FamilyMember familyMember = FamilyMember.builder()
//                .id(id)
//                .firstName(firstName)
//                .lastName(lastName)
//                .birthday(birthday)
//                .dateOfDeath(dateOfDeath)
//                .primaryParent(father)
//                .secondaryParent(mother)
//                .build();
//
//        FamilyMemberResponseDTO familyMemberResponseDTO = FamilyMemberMapper.INSTANCE
//                .familyMemberToFamilyMemberResponseDTO(familyMember);
//
//        assertEquals(familyMember.getId(), familyMemberResponseDTO.getId());
//        assertEquals(familyMember.getFirstName(), familyMemberResponseDTO.getFirstName());
//        assertEquals(familyMember.getLastName(), familyMemberResponseDTO.getLastName());
//        assertEquals(familyMember.getBirthday(), familyMemberResponseDTO.getBirthday());
//        assertEquals(familyMember.getDateOfDeath(), familyMemberResponseDTO.getDateOfDeath());
//        assertEquals(familyMember.getSecondaryParent().getId(), familyMemberResponseDTO.getSecondaryParentId());
//        assertNull(familyMemberResponseDTO.getPrimaryParentId());
//    }
//
//    @Test
//    void familyMemberToFamilyMemberResponseDTOPartners() {
//        FamilyMember familyMember = FamilyMember.builder()
//                .id(id)
//                .firstName(firstName)
//                .lastName(lastName)
//                .birthday(birthday)
//                .dateOfDeath(dateOfDeath)
//                .primaryParent(father)
//                .secondaryParent(mother)
//                .build();
//
//        Long partnerMotherId = 11L;
//        Long partnerFatherId = 15L;
//        List<Long> partnerIds = Arrays.asList(partnerMotherId, partnerFatherId);
//
//        List<FamilyMember> members = Arrays.asList(
//                FamilyMember.builder()
//                        .primaryParent(familyMember)
//                        .secondaryParent(FamilyMember.builder().id(partnerMotherId).build())
//                        .build(),
//                FamilyMember.builder()
//                        .primaryParent(FamilyMember.builder().id(partnerFatherId).build())
//                        .secondaryParent(familyMember)
//                        .build()
//        );
//
//        FamilyMemberResponseDTO familyMemberResponseDTO = FamilyMemberMapper.INSTANCE
//                .familyMemberToFamilyMemberResponseDTO(familyMember, members);
//
//        assertEquals(familyMember.getId(), familyMemberResponseDTO.getId());
//        assertEquals(familyMember.getFirstName(), familyMemberResponseDTO.getFirstName());
//        assertEquals(familyMember.getLastName(), familyMemberResponseDTO.getLastName());
//        assertEquals(familyMember.getBirthday(), familyMemberResponseDTO.getBirthday());
//        assertEquals(familyMember.getDateOfDeath(), familyMemberResponseDTO.getDateOfDeath());
//        assertEquals(familyMember.getSecondaryParent().getId(), familyMemberResponseDTO.getSecondaryParentId());
//        assertEquals(partnerIds, familyMemberResponseDTO.getPartners());
//        assertNull(familyMemberResponseDTO.getPrimaryParentId());
//    }
//
//    @Test
//    void familyMemberDataDTOToFamilyMember() {
//        FamilyMemberDataDTO familyMemberDataDTO =
//                new FamilyMemberDataDTO(firstName, lastName, birthday, dateOfDeath, Gender.MALE, null, motherId);
//
//        FamilyMember familyMember = FamilyMemberMapper.INSTANCE
//                .FamilyMemberDataDTOToFamilyMember(familyMemberDataDTO);
//
//        assertNull(familyMember.getId());
//        assertEquals(familyMemberDataDTO.getFirstName(), familyMember.getFirstName());
//        assertEquals(familyMemberDataDTO.getLastName(), familyMember.getLastName());
//        assertEquals(familyMemberDataDTO.getBirthday(), familyMember.getBirthday());
//        assertEquals(familyMemberDataDTO.getDateOfDeath(), familyMember.getDateOfDeath());
//        assertEquals(familyMemberDataDTO.getGender(), familyMember.getGender());
//        assertEquals(familyMemberDataDTO.getPrimaryParentId(), familyMember.getPrimaryParent().getId());
//        assertEquals(familyMemberDataDTO.getSecondaryParentId(), familyMember.getSecondaryParent().getId());
//        assertNull(familyMember.getFamilyTree());
//    }
//}