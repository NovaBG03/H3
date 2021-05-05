package com.example.h3server.mappers;

import com.example.h3server.dtos.fact.FactDataDTO;
import com.example.h3server.dtos.fact.FactResponseDTO;
import com.example.h3server.models.Fact;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FactMapper {
    FactMapper INSTANCE = Mappers.getMapper(FactMapper.class);

    @Mapping(target = "familyMemberId", source = "familyMember.id")
    FactResponseDTO factToFactResponseDTO(Fact fact);

    Fact factDataDTOToFact(FactDataDTO factDataDTO);
}
