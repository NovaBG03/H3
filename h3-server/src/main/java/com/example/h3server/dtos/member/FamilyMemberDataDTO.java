package com.example.h3server.dtos.member;

import com.example.h3server.models.Gender;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FamilyMemberDataDTO {

    @ApiModelProperty(position = 0)
    private String firstName;

    @ApiModelProperty(position = 1)
    private String lastName;

    @ApiModelProperty(position = 2)
    private LocalDate birthday;

    @ApiModelProperty(position = 3)
    private LocalDate dateOfDeath;

    @ApiModelProperty(position = 4)
    private Gender gender;
}
