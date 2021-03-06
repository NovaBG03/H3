package com.example.h3server.dtos.tree;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FamilyTreeListDTO {

    @ApiModelProperty(position = 0)
    @JsonProperty("familyTrees")
    private List<FamilyTreeResponseDTO> familyTreeDTOs;

    public FamilyTreeListDTO(List<FamilyTreeResponseDTO> familyTreeDTOs) {
        this.familyTreeDTOs = familyTreeDTOs;
    }
}
