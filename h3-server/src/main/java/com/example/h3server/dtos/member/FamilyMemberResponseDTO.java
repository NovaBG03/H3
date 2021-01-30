package com.example.h3server.dtos.member;

import com.example.h3server.models.Gender;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMemberResponseDTO {

    @ApiModelProperty(position = 0)
    private Long id;

    @ApiModelProperty(position = 1)
    private String firstName;

    @ApiModelProperty(position = 2)
    private String lastName;

    @ApiModelProperty(position = 3)
    private LocalDate birthday;

    @ApiModelProperty(position = 4)
    private LocalDate dateOfDeath;

    @ApiModelProperty(position = 5)
    private Gender gender;
}
