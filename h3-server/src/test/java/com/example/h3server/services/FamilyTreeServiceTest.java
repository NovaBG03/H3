package com.example.h3server.services;

import com.example.h3server.exception.CustomException;
import com.example.h3server.models.FamilyTree;
import com.example.h3server.models.Role;
import com.example.h3server.models.User;
import com.example.h3server.repositories.FamilyTreeRepository;
import com.example.h3server.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FamilyTreeServiceTest {

    @Mock
    FamilyTreeRepository familyTreeRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    FamilyTreeService familyTreeService;

    String username = "username";
    User user;
    Long privateTreeId = 1L;
    String privateTreeName = "Private Tree";
    Long publicTreeId = 2L;
    String publicTreeName = "Public Tree";
    Long newTreeId = 2L;
    String newTreeName = "Public Tree";
    Boolean newTreeIsPrivate = false;
    LocalDateTime treeCreatedAt = LocalDateTime.now();
    FamilyTree newFamilyTree;
    List<FamilyTree> familyTrees = new ArrayList<>();

    @BeforeEach
    void setUp() {
        newFamilyTree = FamilyTree.builder()
                .id(newTreeId)
                .name(newTreeName)
                .isPrivate(newTreeIsPrivate)
                .build();

        familyTrees.add(FamilyTree.builder()
                .id(privateTreeId)
                .name(privateTreeName)
                .isPrivate(true)
                .createdAt(treeCreatedAt)
                .build());

        familyTrees.add(FamilyTree.builder()
                .id(publicTreeId)
                .name(publicTreeName)
                .isPrivate(false)
                .createdAt(treeCreatedAt)
                .build());

        user = User.builder()
                .id(1L)
                .username(username)
                .email("email")
                .password("password")
                .roles(Arrays.asList(Role.ROLE_USER))
                .familyTrees(new HashSet<>(familyTrees))
                .build();
    }

    @Test
    void getOwnFamilyTrees() {
        // given
        given(userRepository.findByUsername(username)).willReturn(user);
        given(familyTreeRepository.findAllByUser(user)).willReturn(familyTrees);

        // when
        List<FamilyTree> foundTrees = familyTreeService.getFamilyTrees(username, username);

        // then
        assertEquals(familyTrees, foundTrees);
    }

    @Test
    void getSomeoneElseFamilyTrees() {
        // given
        String anotherUsername = "anotherUsername";
        user.setUsername(anotherUsername);
        given(userRepository.findByUsername(anotherUsername)).willReturn(user);
        given(familyTreeRepository.findAllByUser(user)).willReturn(familyTrees);

        // when
        List<FamilyTree> foundTrees = familyTreeService.getFamilyTrees(anotherUsername, username);

        // then
        assertEquals(1, foundTrees.size());

        FamilyTree publicTree = foundTrees.get(0);
        assertEquals(publicTreeId, publicTree.getId());
        assertEquals(publicTreeName, publicTree.getName());
        assertFalse(publicTree.getIsPrivate());
    }

    @Test
    void getFamilyTreesInvalidUser() {
        // given
        String invalidUsername = "invalidUsername";
        given(userRepository.findByUsername(invalidUsername)).willReturn(null);

        // when
        // then
        CustomException exception = assertThrows(CustomException.class,
                () -> familyTreeService.getFamilyTrees(invalidUsername, username));
        assertEquals("The user doesn't exist", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    void createNewFamilyTree() {
        // given
        ArgumentCaptor<FamilyTree> treeCaptor = ArgumentCaptor.forClass(FamilyTree.class);
        FamilyTree familyTreeToSave = FamilyTree.builder().name(newTreeName).isPrivate(newTreeIsPrivate).build();
        given(userRepository.findByUsername(username)).willReturn(user);
        given(familyTreeRepository.save(treeCaptor.capture())).willReturn(newFamilyTree);

        // when
        FamilyTree createdTree = familyTreeService.createNewFamilyTree(familyTreeToSave, username);

        // then
        FamilyTree capturedTree = treeCaptor.getValue();
        assertNull(capturedTree.getId());
        assertNotNull(capturedTree.getCreatedAt());
        assertEquals(user, capturedTree.getUser());

        assertEquals(newFamilyTree, createdTree);
    }

    @Test
    void updateFamilyTreeExists() {
        // given
        String newName = "new name";
        Boolean newIsPrivate = false;
        FamilyTree newTree = FamilyTree.builder().name(newName).isPrivate(newIsPrivate).build();

        FamilyTree tree = familyTrees.get(0);
        ArgumentCaptor<FamilyTree> treeCaptor = ArgumentCaptor.forClass(FamilyTree.class);

        given(familyTreeRepository.findByIdAndUserUsername(tree.getId(), username)).willReturn(tree);
        given(familyTreeRepository.save(treeCaptor.capture())).willReturn(newFamilyTree);

        // when
        FamilyTree updatedTree = familyTreeService.updateFamilyTree(tree.getId(), newTree, username);

        // then
        FamilyTree capturedTree = treeCaptor.getValue();
        assertNotNull(capturedTree.getId());
        assertEquals(newName, capturedTree.getName());
        assertEquals(newIsPrivate, capturedTree.getIsPrivate());

        assertEquals(newFamilyTree, updatedTree);
    }

    @Test
    void updateFamilyTreeDoesNotExist() {
        // given
        String newName = "new name";
        Boolean newIsPrivate = false;
        FamilyTree newTree = FamilyTree.builder().name(newName).isPrivate(newIsPrivate).build();
        FamilyTree tree = familyTrees.get(0);

        given(familyTreeRepository.findByIdAndUserUsername(tree.getId(), username)).willReturn(null);

        // when
        // then
        CustomException exception = assertThrows(CustomException.class,
                () -> familyTreeService.updateFamilyTree(tree.getId(), newTree, username));

        assertEquals("The family tree doesn't exist", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    void deleteFamilyTreeExists() {
        // given
        FamilyTree tree = familyTrees.get(0);
        given(familyTreeRepository.findByIdAndUserUsername(tree.getId(), username)).willReturn(tree);

        // when
        familyTreeService.deleteFamilyTree(tree.getId(), username);

        // then
        verify(familyTreeRepository, times(1)).delete(tree);
    }

    @Test
    void deleteFamilyTreeDoesNotExist() {
        // given
        FamilyTree tree = familyTrees.get(0);
        given(familyTreeRepository.findByIdAndUserUsername(tree.getId(), username)).willReturn(null);

        // when
        // then
        CustomException exception = assertThrows(CustomException.class,
                () -> familyTreeService.deleteFamilyTree(tree.getId(), username));

        assertEquals("The family tree doesn't exist", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }
}