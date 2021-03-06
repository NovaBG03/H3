//package com.example.h3server.services;
//
//import com.example.h3server.exception.CustomException;
//import com.example.h3server.models.FamilyMember;
//import com.example.h3server.models.FamilyTree;
//import com.example.h3server.models.Role;
//import com.example.h3server.models.User;
//import com.example.h3server.repositories.FamilyMemberRepository;
//import com.example.h3server.repositories.FamilyTreeRepository;
//import com.example.h3server.repositories.UserRepository;
//import com.google.common.collect.Sets;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//@ExtendWith(MockitoExtension.class)
//class FamilyMemberServiceTest {
//
//    @Mock
//    FamilyMemberRepository familyMemberRepository;
//
//    @Mock
//    FamilyTreeRepository familyTreeRepository;
//
//    @Mock
//    UserRepository userRepository;
//
//    @InjectMocks
//    FamilyMemberService familyMemberService;
//
//    Long userId = 5L;
//    String randomUsername = "randomUsername";
//    String username = "username";
//    String email = "email@main.com";
//    String encodedPassword = "encodedPassword";
//    Set<Role> userRoles = Sets.newHashSet(Role.ROLE_USER);
//    User user;
//    User randomUser;
//
//    Long treeId = 10L;
//    String treeName = "tree name";
//    Boolean treeIsPrivate = true;
//    LocalDateTime treeCreatedAt = LocalDateTime.of(2020, 9, 9, 21, 43);
//    FamilyTree familyTree;
//
//    List<FamilyMember> familyMembers = new ArrayList<>();
//
//    FamilyMember father;
//    Long fatherId = 2L;
//    String fatherFirstName = "father";
//    String fatherLastName = "test";
//    LocalDate fatherBirthday = LocalDate.of(2000, 2, 11);
//
//    FamilyMember son;
//    Long sonId = 15L;
//    String sonFirstName = "son";
//    String sonLastName = "test";
//    LocalDate sonBirthday = LocalDate.of(2020, 1, 22);
//
//    FamilyMember newMember;
//    Long newMemberId = 225L;
//    String newMemberFirstName = "newMember";
//    String newMemberLastName = "test";
//    LocalDate newMemberBirthday = LocalDate.of(2020, 5, 14);
//
//
//    @BeforeEach
//    void setUp() {
//        randomUser = User.builder()
//                .username(randomUsername)
//                .roles(Sets.newHashSet(Role.ROLE_USER))
//                .build();
//
//        user = User.builder()
//                .id(userId)
//                .username(username)
//                .email(email)
//                .password(encodedPassword)
//                .roles(userRoles)
//                .build();
//
//        familyTree = FamilyTree.builder()
//                .id(treeId)
//                .name(treeName)
//                .isPrivate(treeIsPrivate)
//                .createdAt(treeCreatedAt)
//                .build();
//        user.addFamilyTree(familyTree);
//
//        father = FamilyMember.builder()
//                .id(fatherId)
//                .firstName(fatherFirstName)
//                .lastName(fatherLastName)
//                .birthday(fatherBirthday)
//                .build();
//        familyTree.addFamilyMember(father);
//        familyMembers.add(father);
//
//        son = FamilyMember.builder()
//                .id(sonId)
//                .firstName(sonFirstName)
//                .lastName(sonLastName)
//                .birthday(sonBirthday)
//                .primaryParent(father)
//                .build();
//        familyTree.addFamilyMember(son);
//        familyMembers.add(son);
//
//        newMember = FamilyMember.builder()
//                .id(newMemberId)
//                .firstName(newMemberFirstName)
//                .lastName(newMemberLastName)
//                .birthday(newMemberBirthday)
//                .primaryParent(FamilyMember.builder().id(fatherId).build())
//                .secondaryParent(FamilyMember.builder().build())
//                .build();
//    }
//
//    @Test
//    void getMembersOwnTree() {
//        // given
//        given(familyTreeRepository.findById(treeId)).willReturn(Optional.of(familyTree));
//        given(familyMemberRepository.findAllByFamilyTreeId(treeId)).willReturn(familyMembers);
//        given(userRepository.findByUsername(username)).willReturn(user);
//
//        // when
//        List<FamilyMember> foundMembers = familyMemberService.getAllCouples(treeId, username);
//
//        // then
//        assertEquals(familyMembers, foundMembers);
//    }
//
//    @Test
//    void getMembersNotOwnPublicTree() {
//        // given
//        familyTree.setIsPrivate(false);
//        given(familyTreeRepository.findById(treeId)).willReturn(Optional.of(familyTree));
//        given(familyMemberRepository.findAllByFamilyTreeId(treeId)).willReturn(familyMembers);
//        given(userRepository.findByUsername(randomUsername)).willReturn(randomUser);
//
//        // when
//        List<FamilyMember> foundMembers = familyMemberService.getAllCouples(treeId, randomUsername);
//
//        // then
//        assertEquals(familyMembers, foundMembers);
//    }
//
//    @Test
//    void getMembersNotOwnPrivateTree() {
//        // given
//        given(familyTreeRepository.findById(treeId)).willReturn(Optional.of(familyTree));
//        given(userRepository.findByUsername(randomUsername)).willReturn(randomUser);
//
//        // when
//        // then
//        CustomException exception = assertThrows(CustomException.class,
//                () -> familyMemberService.getAllCouples(treeId, randomUsername));
//
//        assertEquals("The family tree doesn't exist", exception.getMessage());
//        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
//    }
//
//    @Test
//    void getMembersTreeDoesNotExists() {
//        // given
//        given(familyTreeRepository.findById(treeId)).willReturn(Optional.empty());
//
//        // when
//        // then
//        CustomException exception = assertThrows(CustomException.class,
//                () -> familyMemberService.getAllCouples(treeId, username));
//
//        assertEquals("The family tree doesn't exist", exception.getMessage());
//        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
//    }
//
//    @Test
//    void addMemberOwnTree() {
//        // given
//        ArgumentCaptor<FamilyMember> memberCaptor = ArgumentCaptor.forClass(FamilyMember.class);
//        given(familyTreeRepository.findById(treeId)).willReturn(Optional.of(familyTree));
//        given(familyMemberRepository.save(memberCaptor.capture())).willReturn(newMember);
//
//        // when
//        FamilyMember addedMember = familyMemberService.addMember(treeId, newMember, username);
//
//        // then
//        FamilyMember capturedMember = memberCaptor.getValue();
//        assertNull(capturedMember.getId());
//        assertNull(capturedMember.getSecondaryParent());
//        assertEquals(father, capturedMember.getPrimaryParent());
//
//        assertEquals(newMember ,addedMember);
//    }
//
//    @Test
//    void addMemberOwnTreeInvalidMotherId() {
//        // given
//        Long invalidId = 0L;
//        newMember.setSecondaryParent(FamilyMember.builder().id(invalidId).build());
//        ArgumentCaptor<FamilyMember> memberCaptor = ArgumentCaptor.forClass(FamilyMember.class);
//        given(familyTreeRepository.findById(treeId)).willReturn(Optional.of(familyTree));
//
//        // when
//        // then
//        CustomException exception = assertThrows(CustomException.class,
//                () -> familyMemberService.addMember(treeId, newMember, username));
//
//        assertEquals("Invalid mother id", exception.getMessage());
//        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
//    }
//
//    @Test
//    void addMemberNotOwnTree() {
//        // given
//        given(familyTreeRepository.findById(treeId)).willReturn(Optional.of(familyTree));
//
//        // when
//        // then
//        CustomException exception = assertThrows(CustomException.class,
//                () -> familyMemberService.addMember(treeId, newMember, randomUsername));
//
//        assertEquals("Access denied", exception.getMessage());
//        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
//    }
//
//    @Test
//    void addMemberTreeDoesNotExists() {
//        // given
//        given(familyTreeRepository.findById(treeId)).willReturn(Optional.empty());
//
//        // when
//        // then
//        CustomException exception = assertThrows(CustomException.class,
//                () -> familyMemberService.addMember(treeId, newMember, username));
//
//        assertEquals("The family tree doesn't exist", exception.getMessage());
//        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
//    }
//
//    @Test
//    void updateMember() {
//        // given
//        Long motherId = 34L;
//        FamilyMember mother = FamilyMember.builder()
//                .id(motherId)
//                .firstName(fatherFirstName)
//                .lastName(fatherLastName)
//                .birthday(fatherBirthday)
//                .build();
//        familyTree.addFamilyMember(mother);
//        newMember.setSecondaryParent(mother);
//        newMember.setPrimaryParent(null);
//
//        ArgumentCaptor<FamilyMember> memberCaptor = ArgumentCaptor.forClass(FamilyMember.class);
//        given(familyTreeRepository.findById(treeId)).willReturn(Optional.of(familyTree));
//        given(familyMemberRepository.save(memberCaptor.capture())).willReturn(newMember);
//
//        // when
//        FamilyMember updatedMember = familyMemberService.updateMember(treeId, sonId, newMember, username);
//
//        // then
//        FamilyMember capturedMember = memberCaptor.getValue();
//        assertEquals(sonId, capturedMember.getId());
//        assertEquals(newMemberFirstName, capturedMember.getFirstName());
//        assertEquals(newMemberLastName, capturedMember.getLastName());
//        assertEquals(newMemberBirthday, capturedMember.getBirthday());
//        assertNull(capturedMember.getPrimaryParent());
//        assertEquals(mother, capturedMember.getSecondaryParent());
//
//        assertEquals(newMember, updatedMember);
//    }
//
//    @Test
//    void updateMemberInvalidMotherId() {
//        // given
//        Long motherId = 34L;
//        FamilyMember mother = FamilyMember.builder()
//                .id(motherId)
//                .firstName(fatherFirstName)
//                .lastName(fatherLastName)
//                .birthday(fatherBirthday)
//                .build();
//        newMember.setSecondaryParent(mother);
//
//        given(familyTreeRepository.findById(treeId)).willReturn(Optional.of(familyTree));
//
//        // when
//        // then
//        CustomException exception = assertThrows(CustomException.class,
//                () -> familyMemberService.updateMember(treeId, sonId, newMember, username));
//
//        assertEquals("Invalid mother id", exception.getMessage());
//        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
//    }
//
//    @Test
//    void updateMemberInvalidMemberId() {
//        // given
//        Long invalidId = 0L;
//        given(familyTreeRepository.findById(treeId)).willReturn(Optional.of(familyTree));
//
//        // when
//        // then
//        CustomException exception = assertThrows(CustomException.class,
//                () -> familyMemberService.updateMember(treeId, invalidId, son, username));
//
//        assertEquals("The family member doesn't exist", exception.getMessage());
//        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
//    }
//
//    @Test
//    void updateMemberNotOwnTree() {
//        // given
//        given(familyTreeRepository.findById(treeId)).willReturn(Optional.of(familyTree));
//
//        // when
//        // then
//        CustomException exception = assertThrows(CustomException.class,
//                () -> familyMemberService.updateMember(treeId, sonId, son, randomUsername));
//
//        assertEquals("Access denied", exception.getMessage());
//        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
//    }
//
//    @Test
//    void updateMemberTreeDoesNotExists() {
//        // given
//        given(familyTreeRepository.findById(treeId)).willReturn(Optional.empty());
//
//        // when
//        // then
//        CustomException exception = assertThrows(CustomException.class,
//                () -> familyMemberService.updateMember(treeId, sonId, son, username));
//
//        assertEquals("The family tree doesn't exist", exception.getMessage());
//        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
//    }
//
//    @Test
//    void deleteMember() {
//        // given
//        ArgumentCaptor<FamilyMember> memberCaptor = ArgumentCaptor.forClass(FamilyMember.class);
//        given(familyTreeRepository.findById(treeId)).willReturn(Optional.of(familyTree));
//
//        // when
//        familyMemberService.deleteMember(treeId, fatherId, username);
//
//        // then
//        verify(familyMemberRepository, times(1)).delete(memberCaptor.capture());
//        FamilyMember capturedMember = memberCaptor.getValue();
//
//        assertEquals(father, capturedMember);
//        assertNull(son.getPrimaryParent());
//    }
//
//    @Test
//    void deleteMemberInvalidMemberId() {
//        // given
//        Long invalidId = 0L;
//        given(familyTreeRepository.findById(treeId)).willReturn(Optional.of(familyTree));
//
//        // when
//        // then
//        CustomException exception = assertThrows(CustomException.class,
//                () -> familyMemberService.deleteMember(treeId, invalidId, username));
//
//        assertEquals("The family member doesn't exist", exception.getMessage());
//        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
//    }
//
//    @Test
//    void deleteMemberNotOwnTree() {
//        // given
//        given(familyTreeRepository.findById(treeId)).willReturn(Optional.of(familyTree));
//
//        // when
//        // then
//        CustomException exception = assertThrows(CustomException.class,
//                () -> familyMemberService.deleteMember(treeId, fatherId, randomUsername));
//
//        assertEquals("Access denied", exception.getMessage());
//        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
//    }
//
//    @Test
//    void deleteMemberTreeDoesNotExists() {
//        // given
//        given(familyTreeRepository.findById(treeId)).willReturn(Optional.empty());
//
//        // when
//        // then
//        CustomException exception = assertThrows(CustomException.class,
//                () -> familyMemberService.deleteMember(treeId, fatherId, username));
//
//        assertEquals("The family tree doesn't exist", exception.getMessage());
//        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
//    }
//}