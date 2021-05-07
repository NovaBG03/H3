package com.example.h3server.dtos.fact;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FactResponseDTO {

    @ApiModelProperty(position = 0)
    private Long id;

    @ApiModelProperty(position = 1)
    private String name;

    @ApiModelProperty(position = 2)
    private String description;

    @ApiModelProperty(position = 3)
    private LocalDate date;

    @ApiModelProperty(position = 4)
    private Long familyMemberId;
}
