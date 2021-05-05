package com.example.h3server.mappers;

import com.example.h3server.dtos.member.FamilyMemberDataDTO;
import com.example.h3server.dtos.member.FamilyMemberResponseDTO;
import com.example.h3server.models.FamilyMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface FamilyMemberMapper {

    FamilyMemberMapper INSTANCE = Mappers.getMapper(FamilyMemberMapper.class);

    FamilyMemberResponseDTO familyMemberToFamilyMemberResponseDTO(FamilyMember familyMember);

    FamilyMember FamilyMemberDataDTOToFamilyMember(FamilyMemberDataDTO familyMemberDataDTO);
}
