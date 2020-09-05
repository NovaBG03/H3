package com.example.h3server.controllers;

import com.example.h3server.dtos.tree.FamilyTreeDTO;
import com.example.h3server.dtos.tree.FamilyTreeListDTO;
import com.example.h3server.mappers.FamilyTreeMapper;
import com.example.h3server.services.FamilyTreeService;
import io.swagger.annotations.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/trees")
@Api(tags = "trees")
public class FamilyTreeController {

    private final FamilyTreeService familyTreeService;

    public FamilyTreeController(FamilyTreeService familyTreeService) {
        this.familyTreeService = familyTreeService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FamilyTreeController.getTrees}",
            response = FamilyTreeListDTO.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The user doesn't exist"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public FamilyTreeListDTO getTrees(Principal principal) {
        List<FamilyTreeDTO> familyTreeDTOs = this.familyTreeService.getFamilyTrees(principal.getName())
                .stream()
                .map(familyTree -> FamilyTreeMapper.INSTANCE.familyTreeToFamilyTreeDTO(familyTree))
                .collect(Collectors.toList());

        return new FamilyTreeListDTO(familyTreeDTOs);
    }

}
