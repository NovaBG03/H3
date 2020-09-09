package com.example.h3server.controllers;

import com.example.h3server.dtos.MessageDTO;
import com.example.h3server.dtos.member.FamilyMemberDataDTO;
import com.example.h3server.dtos.member.FamilyMemberListDTO;
import com.example.h3server.dtos.member.FamilyMemberResponseDTO;
import com.example.h3server.mappers.FamilyMemberMapper;
import com.example.h3server.models.FamilyMember;
import com.example.h3server.services.FamilyMemberService;
import io.swagger.annotations.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/trees/{treeId}/members")
@Api(tags = "family members")
public class FamilyMemberController {

    private final FamilyMemberService familyMemberService;

    public FamilyMemberController(FamilyMemberService familyMemberService) {
        this.familyMemberService = familyMemberService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FamilyMemberController.getMembers}",
            response = FamilyMemberListDTO.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The family tree doesn't exist"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public FamilyMemberListDTO getMembers(@PathVariable Long treeId, Principal principal) {
        List<FamilyMemberResponseDTO> familyMemberResponseDTOs = familyMemberService
                .getMembers(treeId, principal.getName())
                .stream()
                .map(familyMember -> FamilyMemberMapper.INSTANCE.familyMemberToFamilyMemberResponseDTO(familyMember))
                .collect(Collectors.toList());

        return new FamilyMemberListDTO(familyMemberResponseDTOs);
    }

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FamilyMemberController.addMember}",
            response = FamilyMemberResponseDTO.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The family tree doesn't exist / " +
                    "Invalid father id / " +
                    "Invalid mother id"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public FamilyMemberResponseDTO addMember(@PathVariable Long treeId,
                                             @RequestBody FamilyMemberDataDTO familyMemberDataDTO,
                                             Principal principal) {
        FamilyMember familyMember = FamilyMemberMapper.INSTANCE.FamilyMemberDataDTOToFamilyMember(familyMemberDataDTO);
        FamilyMember newMember = familyMemberService.addMember(treeId, familyMember, principal.getName());
        return FamilyMemberMapper.INSTANCE.familyMemberToFamilyMemberResponseDTO(newMember);
    }

    @PutMapping("/{memberId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FamilyMemberController.updateMember}",
            response = FamilyMemberResponseDTO.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The family tree doesn't exist / " +
                    "The family member doesn't exist / " +
                    "Invalid father id / " +
                    "Invalid mother id"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public FamilyMemberResponseDTO updateMember(@PathVariable Long treeId,
                                                @PathVariable Long memberId,
                                                @RequestBody FamilyMemberDataDTO familyMemberDataDTO,
                                                Principal principal) {
        FamilyMember familyMember = FamilyMemberMapper.INSTANCE.FamilyMemberDataDTOToFamilyMember(familyMemberDataDTO);
        FamilyMember newMember = familyMemberService.updateMember(treeId, memberId, familyMember, principal.getName());
        return FamilyMemberMapper.INSTANCE.familyMemberToFamilyMemberResponseDTO(newMember);
    }

    @DeleteMapping("/{memberId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FamilyMemberController.deleteMember}",
            response = MessageDTO.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The family tree doesn't exist / " +
                    "The family member doesn't exist"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public MessageDTO deleteMember(@PathVariable Long treeId,
                                   @PathVariable Long memberId,
                                   @ApiIgnore Principal principal) {
        familyMemberService.deleteMember(treeId, memberId, principal.getName());
        return new MessageDTO("Family Member deleted successfully");
    }
}
