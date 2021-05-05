package com.example.h3server.controllers;

import com.example.h3server.dtos.MessageDTO;
import com.example.h3server.dtos.fact.FactDataDTO;
import com.example.h3server.dtos.fact.FactListDTO;
import com.example.h3server.dtos.fact.FactResponseDTO;
import com.example.h3server.mappers.FactMapper;
import com.example.h3server.services.FactService;
import com.sun.prism.impl.FactoryResetException;
import io.swagger.annotations.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
            response = FactListDTO.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The family tree doesn't exist"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public FactListDTO getAllFacts(@PathVariable Long treeId, @ApiIgnore Principal principal) {
        return new FactListDTO(this.factService.getFacts(treeId, principal.getName())
                .stream()
                .map(fact -> FactMapper.INSTANCE.factToFactResponseDTO(fact))
                .collect(Collectors.toList()));
    }

    @PostMapping("/facts")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FactController.createFact}",
            response = FactoryResetException.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The family tree doesn't exist"),
            @ApiResponse(code = 404, message = "Invalid Family Member Id"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public FactResponseDTO createFact(@PathVariable Long treeId,
                                      @RequestBody FactDataDTO factDataDTO,
                                      @RequestParam("familyMemberId") Long familyMemberId,
                                      @ApiIgnore Principal principal) {
        return FactMapper.INSTANCE.factToFactResponseDTO(this.factService.createFact(
                treeId,
                familyMemberId,
                FactMapper.INSTANCE.factDataDTOToFact(factDataDTO),
                principal.getName()
        ));
    }

    @PutMapping("/facts/{factId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FactController.updateFact}",
            response = FactoryResetException.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The family tree doesn't exist"),
            @ApiResponse(code = 404, message = "Invalid Family Member Id"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public FactResponseDTO updateFact(@PathVariable Long treeId,
                                      @PathVariable Long factId,
                                      @RequestBody FactDataDTO factDataDTO,
                                      @RequestParam("familyMemberId") Long familyMemberId,
                                      @ApiIgnore Principal principal) {
        return FactMapper.INSTANCE.factToFactResponseDTO(this.factService.updateFact(
                treeId,
                factId,
                familyMemberId,
                FactMapper.INSTANCE.factDataDTOToFact(factDataDTO),
                principal.getName()
        ));
    }

    @DeleteMapping("/facts/{factId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FactController.deleteFact}",
            response = MessageDTO.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The family tree doesn't exist"),
            @ApiResponse(code = 404, message = "Fact does not exists"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public MessageDTO deleteFact(@PathVariable Long treeId,
                                 @PathVariable Long factId,
                                 @ApiIgnore Principal principal) {
        this.factService.deleteFact(treeId, factId, principal.getName());
        return new MessageDTO("Fact deleted successfully");
    }
}
