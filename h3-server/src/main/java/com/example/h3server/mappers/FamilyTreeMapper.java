package com.example.h3server.mappers;

import com.example.h3server.dtos.tree.FamilyTreeDataDTO;
import com.example.h3server.dtos.tree.FamilyTreeResponseDTO;
import com.example.h3server.models.FamilyTree;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FamilyTreeMapper {

    FamilyTreeMapper INSTANCE = Mappers.getMapper(FamilyTreeMapper.class);

    FamilyTreeResponseDTO familyTreeToFamilyTreeResponseDTO(FamilyTree familyTree);

    FamilyTree familyTreeDataDTOToFamilyTree(FamilyTreeDataDTO familyTreeDataDTO);
}
