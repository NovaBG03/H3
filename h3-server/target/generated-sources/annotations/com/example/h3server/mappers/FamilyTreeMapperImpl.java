package com.example.h3server.mappers;

import com.example.h3server.dtos.tree.FamilyTreeDTO;
import com.example.h3server.models.FamilyTree;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-09-05T20:19:27+0300",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_251 (Oracle Corporation)"
)
public class FamilyTreeMapperImpl implements FamilyTreeMapper {

    @Override
    public FamilyTreeDTO familyTreeToFamilyTreeDTO(FamilyTree familyTree) {
        if ( familyTree == null ) {
            return null;
        }

        FamilyTreeDTO familyTreeDTO = new FamilyTreeDTO();

        familyTreeDTO.setId( familyTree.getId() );
        familyTreeDTO.setName( familyTree.getName() );
        familyTreeDTO.setCreatedAt( familyTree.getCreatedAt() );

        return familyTreeDTO;
    }
}
