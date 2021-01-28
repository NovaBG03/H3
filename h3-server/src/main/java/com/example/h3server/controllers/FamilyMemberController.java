package com.example.h3server.controllers;

import com.example.h3server.dtos.MessageDTO;
import com.example.h3server.dtos.couple.CoupleListDTO;
import com.example.h3server.dtos.member.FamilyMemberDataDTO;
import com.example.h3server.dtos.member.FamilyMemberListDTO;
import com.example.h3server.dtos.member.FamilyMemberResponseDTO;
import com.example.h3server.mappers.CoupleMapper;
import com.example.h3server.services.FamilyMemberService;
import io.swagger.annotations.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
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
            response = CoupleListDTO.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The family tree doesn't exist"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public CoupleListDTO getMembers(@PathVariable Long treeId, @ApiIgnore Principal principal) {
        // todo fix docs
        return new CoupleListDTO(familyMemberService.getAllCouples(treeId, principal.getName())
                .stream()
                .map(couple -> CoupleMapper.INSTANCE.coupleToCoupleResponseDTO(couple))
                .collect(Collectors.toList()));
    }

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FamilyMemberController.addMember}",
            response = FamilyMemberResponseDTO.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong / " +
                    "First name must be from 3 to 225 symbols / " +
                    "Last name must be from 3 to 225 symbols"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The family tree doesn't exist / " +
                    "Invalid father id / " +
                    "Invalid mother id"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public FamilyMemberResponseDTO addMember(@PathVariable Long treeId,
                                             @RequestBody FamilyMemberDataDTO familyMemberDataDTO,
                                             @ApiIgnore Principal principal) {
        return null;
        // todo fix

        // FamilyMember familyMember = FamilyMemberMapper.INSTANCE.FamilyMemberDataDTOToFamilyMember(familyMemberDataDTO);
        // FamilyMember newMember = familyMemberService.addMember(treeId, familyMember, principal.getName());
        // return FamilyMemberMapper.INSTANCE.familyMemberToFamilyMemberResponseDTO(newMember);
    }

    @PutMapping("/{memberId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FamilyMemberController.updateMember}",
            response = FamilyMemberResponseDTO.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong / " +
                    "First name must be from 3 to 225 symbols / " +
                    "Last name must be from 3 to 225 symbols"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The family tree doesn't exist / " +
                    "The family member doesn't exist / " +
                    "Invalid father id / " +
                    "Invalid mother id"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public FamilyMemberResponseDTO updateMember(@PathVariable Long treeId,
                                                @PathVariable Long memberId,
                                                @RequestBody FamilyMemberDataDTO familyMemberDataDTO,
                                                @ApiIgnore Principal principal) {
        // FamilyMember familyMember = FamilyMemberMapper.INSTANCE.FamilyMemberDataDTOToFamilyMember(familyMemberDataDTO);
        // FamilyMember newMember = familyMemberService.updateMember(treeId, memberId, familyMember, principal.getName());
        // return FamilyMemberMapper.INSTANCE.familyMemberToFamilyMemberResponseDTO(newMember);
        return null;
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
