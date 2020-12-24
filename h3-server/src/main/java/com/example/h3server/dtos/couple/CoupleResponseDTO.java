package com.example.h3server.dtos.couple;

import com.example.h3server.models.FamilyTree;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoupleResponseDTO {

    @ApiModelProperty(position = 0)
    private Long primaryParentId;

    @ApiModelProperty(position = 1)
    private Long partnerParentId;

    @ApiModelProperty(position = 2)
    private String primaryParentName;

    @ApiModelProperty(position = 3)
    private String partnerParentName;

    @ApiModelProperty(position = 4)
    private Long treeId;

    @ApiModelProperty(position = 5)
    private Integer leftIndex;

    @ApiModelProperty(position = 6)
    private Integer rightIndex;

    @ApiModelProperty(position = 7)
    private Integer depthIndex;
}
