package com.example.h3server.controllers;

import com.example.h3server.dtos.MessageDTO;
import com.example.h3server.dtos.tree.FamilyTreeDataDTO;
import com.example.h3server.dtos.tree.FamilyTreeResponseDTO;
import com.example.h3server.dtos.tree.FamilyTreeListDTO;
import com.example.h3server.mappers.FamilyTreeMapper;
import com.example.h3server.models.FamilyTree;
import com.example.h3server.services.FamilyTreeService;
import io.swagger.annotations.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/trees")
@Api(tags = "trees")
public class FamilyTreeController {

    private final FamilyTreeService familyTreeService;

    public FamilyTreeController(FamilyTreeService familyTreeService) {
        this.familyTreeService = familyTreeService;
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FamilyTreeController.getTrees}",
            response = FamilyTreeListDTO.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The user doesn't exist"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public FamilyTreeListDTO getTrees(@PathVariable String username, @ApiIgnore Principal principal) {
        List<FamilyTreeResponseDTO> familyTreeDTOs = this.familyTreeService.getFamilyTrees(username, principal.getName())
                .stream()
                .map(familyTree -> FamilyTreeMapper.INSTANCE.familyTreeToFamilyTreeResponseDTO(familyTree))
                .collect(Collectors.toList());

        return new FamilyTreeListDTO(familyTreeDTOs);
    }

    @GetMapping("/id/{treeId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FamilyTreeController.getTree}",
            response = FamilyTreeResponseDTO.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The family tree doesn't exist"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public FamilyTreeResponseDTO getTree(@PathVariable Long treeId, @ApiIgnore Principal principal) {
        return FamilyTreeMapper.INSTANCE
                .familyTreeToFamilyTreeResponseDTO(
                        this.familyTreeService.getFamilyTree(treeId, principal.getName()));
    }

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FamilyTreeController.createNewTree}",
            response = FamilyTreeResponseDTO.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong / FamilyTree name must be from 3 to 225 symbols"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public FamilyTreeResponseDTO createNewTree(@RequestBody FamilyTreeDataDTO familyTreeDataDTO,
                                               @ApiIgnore Principal principal) {
        FamilyTree familyTree = FamilyTreeMapper.INSTANCE.familyTreeDataDTOToFamilyTree(familyTreeDataDTO);

        return FamilyTreeMapper.INSTANCE
                .familyTreeToFamilyTreeResponseDTO(
                        this.familyTreeService.createNewFamilyTree(familyTree, principal.getName()));
    }

    @PutMapping("/{treeId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FamilyTreeController.updateTree}",
            response = FamilyTreeResponseDTO.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong / FamilyTree name must be from 3 to 225 symbols"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The family tree doesn't exist"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public FamilyTreeResponseDTO updateTree(@PathVariable Long treeId,
                                            @RequestBody FamilyTreeDataDTO familyTreeDataDTO ,
                                            @ApiIgnore Principal principal) {
        FamilyTree familyTree = FamilyTreeMapper.INSTANCE.familyTreeDataDTOToFamilyTree(familyTreeDataDTO);

        return FamilyTreeMapper.INSTANCE
                .familyTreeToFamilyTreeResponseDTO(
                        this.familyTreeService.updateFamilyTree(treeId, familyTree, principal.getName()));
    }

    @DeleteMapping("/{treeId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FamilyTreeController.deleteTree}",
            response = MessageDTO.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The family tree doesn't exist"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public MessageDTO deleteTree(@PathVariable Long treeId, @ApiIgnore Principal principal) {
        this.familyTreeService.deleteFamilyTree(treeId, principal.getName());
        return new MessageDTO("Family Tree deleted successfully");
    }

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FamilyTreeController.findTrees}",
            response = FamilyTreeListDTO.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The user doesn't exist"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public FamilyTreeListDTO findTrees(@RequestParam String treePattern, @ApiIgnore Principal principal) {
        List<FamilyTreeResponseDTO> familyTreeDTOs = this.familyTreeService.findFamilyTrees(treePattern, principal.getName())
                .stream()
                .map(familyTree -> FamilyTreeMapper.INSTANCE.familyTreeToFamilyTreeResponseDTO(familyTree))
                .collect(Collectors.toList());

        return new FamilyTreeListDTO(familyTreeDTOs);
    }
}
