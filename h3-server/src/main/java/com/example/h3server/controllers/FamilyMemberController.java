package com.example.h3server.controllers;

import com.example.h3server.dtos.ImageDTO;
import com.example.h3server.dtos.MessageDTO;
import com.example.h3server.dtos.couple.CoupleListDTO;
import com.example.h3server.dtos.member.FamilyMemberDataDTO;
import com.example.h3server.dtos.member.FamilyMemberListDTO;
import com.example.h3server.dtos.member.FamilyMemberResponseDTO;
import com.example.h3server.mappers.CoupleMapper;
import com.example.h3server.mappers.FamilyMemberMapper;
import com.example.h3server.models.FamilyMember;
import com.example.h3server.services.FamilyMemberService;
import io.swagger.annotations.*;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
    public FamilyMemberListDTO getMembers(@PathVariable Long treeId, @ApiIgnore Principal principal) {
        return new FamilyMemberListDTO(familyMemberService.getAllMembers(treeId, principal.getName())
                .stream()
                .map(member -> FamilyMemberMapper.INSTANCE.familyMemberToFamilyMemberResponseDTO(member))
                .collect(Collectors.toList()));
    }

    @GetMapping("/couples")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FamilyMemberController.getCouples}",
            response = CoupleListDTO.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The family tree doesn't exist"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public CoupleListDTO getCouples(@PathVariable Long treeId, @ApiIgnore Principal principal) {
        return new CoupleListDTO(familyMemberService.getAllCouples(treeId, principal.getName())
                .stream()
                .map(couple -> CoupleMapper.INSTANCE.coupleToCoupleResponseDTO(couple))
                .collect(Collectors.toList()));
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
        FamilyMember familyMember = FamilyMemberMapper.INSTANCE.FamilyMemberDataDTOToFamilyMember(familyMemberDataDTO);
        FamilyMember newMember = familyMemberService.updateMember(treeId, memberId, familyMember, principal.getName());
        return FamilyMemberMapper.INSTANCE.familyMemberToFamilyMemberResponseDTO(newMember);
    }

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FamilyMemberController.addMember}",
            response = FamilyMemberResponseDTO.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong / " +
                    "Invalid parent id / " +
                    "First name must be from 3 to 225 symbols / " +
                    "Last name must be from 3 to 225 symbols"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The family tree doesn't exist"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public FamilyMemberResponseDTO addMember(@PathVariable Long treeId,
                                             @RequestParam Long primaryParentId,
                                             @RequestParam Long partnerParentId,
                                             @RequestBody FamilyMemberDataDTO familyMemberDataDTO,
                                             @ApiIgnore Principal principal) {
        FamilyMember familyMember = FamilyMemberMapper.INSTANCE.FamilyMemberDataDTOToFamilyMember(familyMemberDataDTO);
        FamilyMember newMember = familyMemberService
                .addMember(treeId, familyMember, primaryParentId, partnerParentId, principal.getName());
        return FamilyMemberMapper.INSTANCE.familyMemberToFamilyMemberResponseDTO(newMember);
    }

    @PostMapping("/partner")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FamilyMemberController.addPartner}",
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
    public FamilyMemberResponseDTO addPartner(@PathVariable Long treeId,
                                              @RequestParam Long primaryParentId,
                                              @RequestBody FamilyMemberDataDTO familyMemberDataDTO,
                                              @ApiIgnore Principal principal) {
        FamilyMember familyMember = FamilyMemberMapper.INSTANCE.FamilyMemberDataDTOToFamilyMember(familyMemberDataDTO);
        FamilyMember newMember = familyMemberService
                .addPartner(treeId, familyMember, primaryParentId, principal.getName());
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

    @PostMapping("/{memberId}/picture")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FamilyMemberController.uploadPicture}",
            response = MessageDTO.class,
            authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public MessageDTO uploadPicture(@RequestParam MultipartFile image,
                                    @PathVariable Long treeId,
                                    @PathVariable Long memberId,
                                    @ApiIgnore Principal principal) {
        this.familyMemberService.updatePicture(image, treeId, memberId, principal.getName());
        return new MessageDTO("Profile picture uploaded successfully");
    }

    @GetMapping("/{memberId}/picture")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "${FamilyMemberController.getPicture}",
            response = ImageDTO.class,
            authorizations = { @Authorization(value="apiKey") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 404, message = "The user doesn't exist"),
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public ImageDTO getPicture(@PathVariable Long treeId,
                               @PathVariable Long memberId,
                               @ApiIgnore Principal principal) {
        return new ImageDTO(this.familyMemberService.getPicture(treeId, memberId, principal.getName()));
    }
}
