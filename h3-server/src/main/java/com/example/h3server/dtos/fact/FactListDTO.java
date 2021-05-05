package com.example.h3server.dtos.fact;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FactListDTO {

    @ApiModelProperty(position = 0)
    @JsonProperty("facts")
    private List<FactResponseDTO> factDTOs;

    public FactListDTO(List<FactResponseDTO> factDTOs) {
        this.factDTOs = factDTOs;
    }
}
