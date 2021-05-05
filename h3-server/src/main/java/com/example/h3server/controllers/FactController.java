package com.example.h3server.controllers;

import com.example.h3server.dtos.couple.CoupleListDTO;
import com.example.h3server.dtos.member.FamilyMemberListDTO;
import com.example.h3server.mappers.FamilyMemberMapper;
import com.example.h3server.services.FactService;
import io.swagger.annotations.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/trees/{treeId}")
@Api(tags = "facts")
public class FactController {

    private final FactService factService;

    public FactController(FactService factService) {
        this.factService = factService;
    }

    @GetMapping("/facts")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FactController.getAllFacts}",
            response = CoupleListDTO.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The family tree doesn't exist"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public FamilyMemberListDTO getAllFacts(@PathVariable Long treeId, @ApiIgnore Principal principal) {
        this.factService.getFacts(treeId, principal.getName());

        return null;
//        return new FamilyMemberListDTO(familyMemberService.getAllMembers(treeId, principal.getName())
//                .stream()
//                .map(member -> FamilyMemberMapper.INSTANCE.familyMemberToFamilyMemberResponseDTO(member))
//                .collect(Collectors.toList()));
    }
}
