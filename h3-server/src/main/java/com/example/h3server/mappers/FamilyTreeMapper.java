package com.example.h3server.mappers;

import com.example.h3server.dtos.tree.FamilyTreeDTO;
import com.example.h3server.models.FamilyTree;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FamilyTreeMapper {

    FamilyTreeMapper INSTANCE = Mappers.getMapper(FamilyTreeMapper.class);

    FamilyTreeDTO familyTreeToFamilyTreeDTO(FamilyTree familyTree);
}
