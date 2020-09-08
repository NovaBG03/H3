package com.example.h3server.dtos.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FamilyMemberListDTO {

    @ApiModelProperty(position = 0)
    @JsonProperty("familyMembers")
    private List<FamilyMemberResponseDTO> familyMemberDTOs;

    public FamilyMemberListDTO(List<FamilyMemberResponseDTO> familyMemberDTOs) {
        this.familyMemberDTOs = familyMemberDTOs;
    }
}
