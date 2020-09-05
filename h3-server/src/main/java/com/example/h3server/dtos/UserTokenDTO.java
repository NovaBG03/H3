package com.example.h3server.dtos;

import com.example.h3server.models.Role;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserTokenDTO {
    @ApiModelProperty(position = 0)
    private String token;

    @ApiModelProperty(position = 1)
    private Date expiresIn;

    @ApiModelProperty(position = 2)
    private Long id;

    @ApiModelProperty(position = 3)
    private String username;

    @ApiModelProperty(position = 4)
    private String email;

    @ApiModelProperty(position = 5)
    private List<Role> roles = new ArrayList<>();

    @Builder
    public UserTokenDTO(String token, Date expiresIn, Long id, String username, String email, List<Role> roles) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.id = id;
        this.username = username;
        this.email = email;
        if (roles != null) {
            this.roles = roles;
        }
    }
}
