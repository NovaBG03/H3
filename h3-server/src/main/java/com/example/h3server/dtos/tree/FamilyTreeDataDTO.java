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
public class FamilyTreeDataDTO {

    @ApiModelProperty(position = 0)
    private String name;

    private Boolean isPrivate;

    @ApiModelProperty(position = 2)
    private List<String> tags;

    @JsonProperty("isPrivate")
    @ApiModelProperty(position = 1)
    public boolean getIsPrivate() {
        return this.isPrivate;
    }
}
