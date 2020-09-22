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

    static List<Long> getPartners(FamilyMember familyMember, List<FamilyMember> familyMembers) {
        Set<Long> parentIds = new HashSet<>();

        List<FamilyMember> childrenWithParents = familyMembers.stream()
                .filter(member -> member.getPrimaryParent() != null && member.getSecondaryParent() != null)
                .collect(Collectors.toList());

        parentIds.addAll(childrenWithParents
                .stream()
                .filter(member -> member.getSecondaryParent().getId().equals(familyMember.getId()))
                .map(member -> member.getPrimaryParent().getId())
                .collect(Collectors.toSet()));

        parentIds.addAll(childrenWithParents
                .stream()
                .filter(member -> member.getPrimaryParent().getId().equals(familyMember.getId()))
                .map(member -> member.getSecondaryParent().getId())
                .collect(Collectors.toSet()));

        return new ArrayList<>(parentIds);
    }

    @Mapping(target = "primaryParentId",
            expression = "java(familyMember.getPrimaryParent() != null ? familyMember.getPrimaryParent().getId() : null)")
    @Mapping(target = "secondaryParentId",
            expression = "java(familyMember.getSecondaryParent() != null ? familyMember.getSecondaryParent().getId() : null)")
    @Mapping(target = "partners",
            expression = "java(FamilyMemberMapper.getPartners(familyMember, familyMembers))")
    FamilyMemberResponseDTO familyMemberToFamilyMemberResponseDTO(FamilyMember familyMember,
                                                                  List<FamilyMember> familyMembers);

    @Mapping(target = "primaryParentId",
            expression = "java(familyMember.getPrimaryParent() != null ? familyMember.getPrimaryParent().getId() : null)")
    @Mapping(target = "secondaryParentId",
            expression = "java(familyMember.getSecondaryParent() != null ? familyMember.getSecondaryParent().getId() : null)")
    FamilyMemberResponseDTO familyMemberToFamilyMemberResponseDTO(FamilyMember familyMember);

    @Mapping(target = "primaryParent",
            expression = "java(FamilyMember.builder().id(familyMemberDataDTO.getPrimaryParentId()).build())")
    @Mapping(target = "secondaryParent",
            expression = "java(FamilyMember.builder().id(familyMemberDataDTO.getSecondaryParentId()).build())")
    FamilyMember FamilyMemberDataDTOToFamilyMember(FamilyMemberDataDTO familyMemberDataDTO);
}
