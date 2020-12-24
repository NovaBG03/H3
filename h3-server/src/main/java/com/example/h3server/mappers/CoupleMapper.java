package com.example.h3server.mappers;

import com.example.h3server.dtos.couple.CoupleResponseDTO;
import com.example.h3server.dtos.tree.FamilyTreeResponseDTO;
import com.example.h3server.models.Couple;
import com.example.h3server.models.FamilyTree;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CoupleMapper {
    CoupleMapper INSTANCE = Mappers.getMapper(CoupleMapper.class);

    @Mapping(target = "treeId", source = "familyTree.id")
    CoupleResponseDTO coupleToCoupleResponseDTO(Couple couple);
}
