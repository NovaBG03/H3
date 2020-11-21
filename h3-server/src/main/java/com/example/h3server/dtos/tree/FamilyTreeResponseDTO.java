package com.example.h3server.dtos.tree;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FamilyTreeResponseDTO {

    @ApiModelProperty(position = 0)
    private Long id;

    @ApiModelProperty(position = 1)
    private String name;

    private Boolean isPrivate;

    @ApiModelProperty(position = 3)
    private LocalDateTime createdAt;

    @ApiModelProperty(position = 4)
    private String owner;

    @ApiModelProperty(position = 5)
    private List<String> tags;

    @JsonProperty("isPrivate")
    @ApiModelProperty(position = 2)
    public boolean getIsPrivate() {
        return this.isPrivate;
    }
}
