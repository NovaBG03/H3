package com.example.h3server.mappers;

import com.example.h3server.dtos.tree.FamilyTreeDataDTO;
import com.example.h3server.dtos.tree.FamilyTreeResponseDTO;
import com.example.h3server.models.FamilyTree;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FamilyTreeMapper {

    FamilyTreeMapper INSTANCE = Mappers.getMapper(FamilyTreeMapper.class);

    @Mapping(target = "owner", source = "user.username")
    @Mapping(target = "tags",
            expression = "java(familyTree.getTags() == null ? com.google.common.collect.Lists.newArrayList() : familyTree.getTags().stream().map(treeTag -> treeTag.getLabel()).collect(java.util.stream.Collectors.toList()))")
    FamilyTreeResponseDTO familyTreeToFamilyTreeResponseDTO(FamilyTree familyTree);

    @Mapping(target = "tags",
            expression = "java(familyTreeDataDTO.getTags() == null ? com.google.common.collect.Sets.newHashSet() : familyTreeDataDTO.getTags().stream().map(tag -> com.example.h3server.models.TreeTag.builder().label(tag).build()).collect(java.util.stream.Collectors.toSet()))")
    FamilyTree familyTreeDataDTOToFamilyTree(FamilyTreeDataDTO familyTreeDataDTO);
}
