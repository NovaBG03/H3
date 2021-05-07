package com.example.h3server.services;

import com.example.h3server.exception.CustomException;
import com.example.h3server.models.Couple;
import com.example.h3server.models.FamilyMember;
import com.example.h3server.models.FamilyTree;
import com.example.h3server.models.User;
import com.example.h3server.repositories.FamilyMemberRepository;
import com.example.h3server.repositories.FamilyTreeRepository;
import com.example.h3server.repositories.UserRepository;
import com.example.h3server.utils.ModelValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class FamilyMemberService {

    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyTreeRepository familyTreeRepository;
    private final UserRepository userRepository;
    private final CoupleService coupleService;

    public FamilyMemberService(FamilyMemberRepository familyMemberRepository,
                               FamilyTreeRepository familyTreeRepository,
                               UserRepository userRepository,
                               CoupleService coupleService) {
        this.familyMemberRepository = familyMemberRepository;
        this.familyTreeRepository = familyTreeRepository;
        this.userRepository = userRepository;
        this.coupleService = coupleService;
    }

    public List<FamilyMember> getAllMembers(Long treeId, String username) {
        final FamilyTree familyTree = getTreeOrThrowException(treeId);
        final User user = userRepository.findByUsername(username);

        checkIfUserHaveAccess(familyTree, user);

        return familyMemberRepository.findAllByFamilyTreeId(treeId);
    }

    public List<Couple> getAllCouples(Long treeId, String username) {
        final FamilyTree familyTree = getTreeOrThrowException(treeId);
        final User user = userRepository.findByUsername(username);

        checkIfUserHaveAccess(familyTree, user);

        return coupleService.getAllCouples(familyTree);
    }

    public FamilyMember updateMember(Long treeId, Long memberId, FamilyMember familyMember, String username) {
        FamilyTree familyTree = getTreeOrThrowException(treeId);

        if (!familyTree.getUser().getUsername().equals(username)) {
            throw new CustomException("Access denied", HttpStatus.FORBIDDEN);
        }

        FamilyMember memberFromDb = null;
        try {
            memberFromDb = this.familyMemberRepository.findByIdAndFamilyTreeId(memberId, treeId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        if (memberFromDb == null) {
            throw new CustomException("The family member doesn't exist", HttpStatus.NOT_FOUND);
        }

        familyMember.setId(memberFromDb.getId());

        ModelValidator.validate(familyMember);
        return familyMemberRepository.save(familyMember);
    }

    public FamilyMember addMember(Long treeId,
                                  FamilyMember familyMember,
                                  Long primaryParentId,
                                  Long partnerParentId,
                                  String username) {
        FamilyTree familyTree = getTreeOrThrowException(treeId);

        if (!familyTree.getUser().getUsername().equals(username)) {
            throw new CustomException("Access denied", HttpStatus.FORBIDDEN);
        }

        if (primaryParentId.equals(0L) && partnerParentId.equals(0L)) {
            if (familyTree.getCouples().size() == 0) {
                return this.addMainMember(treeId, familyMember, username);
            } else {
                throw new CustomException("Invalid parent id", HttpStatus.BAD_REQUEST);
            }
        }

        familyMember.setId(null);

        ModelValidator.validate(familyMember);
        final FamilyMember savedMember = familyMemberRepository.save(familyMember);

        coupleService.addChild(familyTree, primaryParentId, partnerParentId, familyMember.getId());

        return savedMember;
    }

    @Transactional
    public FamilyMember addPartner(Long treeId, FamilyMember familyMember, Long primaryParentId, String username) {
        FamilyTree familyTree = getTreeOrThrowException(treeId);

        if (!familyTree.getUser().getUsername().equals(username)) {
            throw new CustomException("Access denied", HttpStatus.FORBIDDEN);
        }

        familyMember.setId(null);
        ModelValidator.validate(familyMember);
        final FamilyMember savedMember = familyMemberRepository.save(familyMember);

        coupleService.addPartner(familyTree, primaryParentId, savedMember.getId());

        return savedMember;
    }

    public FamilyMember addMainMember(Long treeId, FamilyMember familyMember, String username) {
        FamilyTree familyTree = getTreeOrThrowException(treeId);

        if (!familyTree.getUser().getUsername().equals(username)) {
            throw new CustomException("Access denied", HttpStatus.FORBIDDEN);
        }

        familyMember.setId(null);

        ModelValidator.validate(familyMember);
        final FamilyMember savedMember = familyMemberRepository.save(familyMember);

        coupleService.addMainMember(familyTree, savedMember.getId());

        return savedMember;
    }

    @Transactional
    public void deleteMember(Long treeId, Long memberId, String username) {

        FamilyTree familyTree = getTreeOrThrowException(treeId);

        if (!familyTree.getUser().getUsername().equals(username)) {
            throw new CustomException("Access denied", HttpStatus.FORBIDDEN);
        }

        Collection<Long> memberIdsToDelete = coupleService.deleteMember(familyTree, memberId);

        for (Long id : memberIdsToDelete) {
            familyMemberRepository.deleteById(id);
        }
    }

    public void updatePicture(MultipartFile image, Long treeId, Long memberId, String username) {
        FamilyTree familyTree = getTreeOrThrowException(treeId);

        if (!familyTree.getUser().getUsername().equals(username)) {
            throw new CustomException("Access denied", HttpStatus.FORBIDDEN);
        }

        FamilyMember member = familyMemberRepository.findByIdAndFamilyTreeId(memberId, treeId);
        if (member == null) {
            throw new CustomException("The family member doesn't exist", HttpStatus.NOT_FOUND);
        }

        try {
            byte[] bytes = image.getBytes();
            int bytesLength = bytes.length;

            Byte[] imageBytes = new Byte[bytesLength];
            for (int i = 0; i < bytes.length; i++) {
                imageBytes[i] = bytes[i];
            }

            member.setPicture(imageBytes);
        } catch (IOException e) {
            //todo catch exception
            log.error(e.getMessage());
        }

        familyMemberRepository.save(member);
    }

    public byte[] getPicture(Long treeId, Long memberId, String username) {
        FamilyTree familyTree = getTreeOrThrowException(treeId);
        final User user = userRepository.findByUsername(username);

        this.checkIfUserHaveAccess(familyTree, user);

        FamilyMember member = familyMemberRepository.findByIdAndFamilyTreeId(memberId, treeId);
        if (member == null) {
            throw new CustomException("The family member doesn't exist", HttpStatus.NOT_FOUND);
        }

        Byte[] bytes = member.getPicture();
        if (bytes == null) {
            return null;
        }

        int bytesLength = bytes.length;

        byte[] imageBytes = new byte[bytesLength];
        for (int i = 0; i < bytesLength; i++ ) {
            imageBytes[i] = bytes[i];
        }

        return imageBytes;
    }

    private FamilyTree getTreeOrThrowException(Long treeId) {
        return familyTreeRepository.findById(treeId)
                .orElseThrow(() -> new CustomException("The family tree doesn't exist", HttpStatus.NOT_FOUND));
    }

    private void checkIfUserHaveAccess(FamilyTree familyTree, User user) {
        if (familyTree.getIsPrivate()
                && !user.getUsername().equals(familyTree.getUser().getUsername())
                && !user.isAdmin()) {
            throw new CustomException("The family tree doesn't exist", HttpStatus.NOT_FOUND);
        }
    }
}
