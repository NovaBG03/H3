package com.example.h3server.mappers;

import com.example.h3server.dtos.tree.FamilyTreeDataDTO;
import com.example.h3server.dtos.tree.FamilyTreeResponseDTO;
import com.example.h3server.models.FamilyTree;
import com.example.h3server.models.FamilyTree.FamilyTreeBuilder;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-09-06T16:33:17+0300",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_251 (Oracle Corporation)"
)
public class FamilyTreeMapperImpl implements FamilyTreeMapper {

    @Override
    public FamilyTreeResponseDTO familyTreeToFamilyTreeResponseDTO(FamilyTree familyTree) {
        if ( familyTree == null ) {
            return null;
        }

        FamilyTreeResponseDTO familyTreeResponseDTO = new FamilyTreeResponseDTO();

        familyTreeResponseDTO.setId( familyTree.getId() );
        familyTreeResponseDTO.setName( familyTree.getName() );
        familyTreeResponseDTO.setIsPrivate( familyTree.getIsPrivate() );
        familyTreeResponseDTO.setCreatedAt( familyTree.getCreatedAt() );

        return familyTreeResponseDTO;
    }

    @Override
    public FamilyTree familyTreeDataDTOToFamilyTree(FamilyTreeDataDTO familyTreeDataDTO) {
        if ( familyTreeDataDTO == null ) {
            return null;
        }

        FamilyTreeBuilder familyTree = FamilyTree.builder();

        familyTree.name( familyTreeDataDTO.getName() );
        familyTree.isPrivate( familyTreeDataDTO.getIsPrivate() );

        return familyTree.build();
    }
}
