package com.example.h3server.mappers;

import com.example.h3server.dtos.tree.FamilyTreeDataDTO;
import com.example.h3server.dtos.tree.FamilyTreeResponseDTO;
import com.example.h3server.models.FamilyTree;
import com.example.h3server.models.TreeTag;
import com.example.h3server.models.User;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FamilyTreeMapperTest {

    private Long id = 1L;
    private String name = "name";
    private Boolean isPrivate = true;
    private LocalDateTime createdAt = LocalDateTime.of(2020, 9, 7, 12, 7);
    private User user = User.builder().build();
    private String tagName1 = "tag-name-1";
    private String tagName2 = "tag-name-2";
    private List<String> tagNames = Arrays.asList(tagName1, tagName2);
    private Set<TreeTag> tags = Sets.newHashSet(
            TreeTag.builder().label(tagName1).build(),
            TreeTag.builder().label(tagName2).build());

    @Test
    void familyTreeToFamilyTreeResponseDTO() {
        FamilyTree familyTree = FamilyTree.builder()
                .id(id)
                .name(name)
                .isPrivate(isPrivate)
                .createdAt(createdAt)
                .user(user)
                .tags(tags)
                .build();

        FamilyTreeResponseDTO familyTreeResponseDTO = FamilyTreeMapper.INSTANCE
                .familyTreeToFamilyTreeResponseDTO(familyTree);

        assertEquals(id, familyTreeResponseDTO.getId());
        assertEquals(name, familyTreeResponseDTO.getName());
        assertEquals(isPrivate, familyTreeResponseDTO.getIsPrivate());
        assertEquals(createdAt, familyTreeResponseDTO.getCreatedAt());
        assertEquals(tagNames.size(), familyTreeResponseDTO.getTags().size());
    }

    @Test
    void familyTreeDataDTOToFamilyTree() {
        FamilyTreeDataDTO familyTreeDataDTO = new FamilyTreeDataDTO(name, isPrivate, tagNames);

        FamilyTree familyTree = FamilyTreeMapper.INSTANCE
                .familyTreeDataDTOToFamilyTree(familyTreeDataDTO);

        assertNull(familyTree.getId());
        assertEquals(name, familyTree.getName());
        assertEquals(isPrivate, familyTree.getIsPrivate());
        assertNull(familyTree.getCreatedAt());
        assertNull(familyTree.getUser());
        assertEquals(tags.size(), familyTree.getTags().size());
    }
}