package com.example.h3server.dtos;

import com.example.h3server.models.Role;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    @ApiModelProperty(position = 0)
    private Long id;

    @ApiModelProperty(position = 1)
    private String username;

    @ApiModelProperty(position = 2)
    private String email;

    @ApiModelProperty(position = 3)
    private List<Role> roles;
}
