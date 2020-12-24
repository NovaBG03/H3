package com.example.h3server.dtos.couple;

import com.example.h3server.dtos.member.FamilyMemberResponseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CoupleListDTO {

    @ApiModelProperty(position = 0)
    @JsonProperty("couples")
    private List<CoupleResponseDTO> coupleDTOs;

    public CoupleListDTO(List<CoupleResponseDTO> coupleDTOs) {
        this.coupleDTOs = coupleDTOs;
    }
}
