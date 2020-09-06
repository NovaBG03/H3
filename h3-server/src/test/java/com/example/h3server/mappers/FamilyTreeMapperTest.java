package com.example.h3server.mappers;

import com.example.h3server.dtos.tree.FamilyTreeDataDTO;
import com.example.h3server.dtos.tree.FamilyTreeResponseDTO;
import com.example.h3server.models.FamilyTree;
import com.example.h3server.models.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FamilyTreeMapperTest {

    private Long id = 1L;
    private String name = "name";
    private Boolean isPrivate = true;
    private LocalDateTime createdAt = LocalDateTime.of(2020, 9, 7, 12, 7);
    private User user = User.builder().build();

    @Test
    void familyTreeToFamilyTreeResponseDTO() {
        FamilyTree familyTree = FamilyTree.builder()
                .id(id)
                .name(name)
                .isPrivate(isPrivate)
                .createdAt(createdAt)
                .user(user)
                .build();

        FamilyTreeResponseDTO familyTreeResponseDTO = FamilyTreeMapper.INSTANCE
                .familyTreeToFamilyTreeResponseDTO(familyTree);

        assertEquals(id, familyTreeResponseDTO.getId());
        assertEquals(name, familyTreeResponseDTO.getName());
        assertEquals(isPrivate, familyTreeResponseDTO.getIsPrivate());
        assertEquals(createdAt, familyTreeResponseDTO.getCreatedAt());
    }

    @Test
    void familyTreeDataDTOToFamilyTree() {
        FamilyTreeDataDTO familyTreeDataDTO = new FamilyTreeDataDTO(name, isPrivate);

        FamilyTree familyTree = FamilyTreeMapper.INSTANCE
                .familyTreeDataDTOToFamilyTree(familyTreeDataDTO);

        assertNull(familyTree.getId());
        assertEquals(name, familyTree.getName());
        assertEquals(isPrivate, familyTree.getIsPrivate());
        assertNull(familyTree.getCreatedAt());
        assertNull(familyTree.getUser());

    }
}