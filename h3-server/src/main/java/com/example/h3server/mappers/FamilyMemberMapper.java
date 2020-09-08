package com.example.h3server.mappers;

import com.example.h3server.dtos.member.FamilyMemberResponseDTO;
import com.example.h3server.models.FamilyMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FamilyMemberMapper {

    FamilyMemberMapper INSTANCE = Mappers.getMapper(FamilyMemberMapper.class);

    @Mapping(target = "fatherId",
            expression = "java(familyMember.getFather() != null ? familyMember.getFather().getId() : null)")
    @Mapping(target = "motherId",
            expression = "java(familyMember.getMother() != null ? familyMember.getMother().getId() : null)")
    FamilyMemberResponseDTO familyMemberToFamilyMemberResponseDTO(FamilyMember familyMember);
}
