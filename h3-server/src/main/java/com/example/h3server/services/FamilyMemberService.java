package com.example.h3server.services;

import com.example.h3server.exception.CustomException;
import com.example.h3server.models.FamilyMember;
import com.example.h3server.models.FamilyTree;
import com.example.h3server.repositories.FamilyMemberRepository;
import com.example.h3server.repositories.FamilyTreeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FamilyMemberService {

    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyTreeRepository familyTreeRepository;

    public FamilyMemberService(FamilyMemberRepository familyMemberRepository,
                               FamilyTreeRepository familyTreeRepository) {
        this.familyMemberRepository = familyMemberRepository;
        this.familyTreeRepository = familyTreeRepository;
    }

    public List<FamilyMember> getMembers(Long treeId, String username) {
        FamilyTree familyTree = getTreeOrThrowException(treeId);

        if (familyTree.getIsPrivate() && !familyTree.getUser().getUsername().equals(username)) {
            throw new CustomException("The family tree doesn't exist", HttpStatus.NOT_FOUND);
        }

        return familyMemberRepository.findAllByFamilyTreeId(treeId);
    }

    public FamilyMember addMember(Long treeId, FamilyMember familyMember, String name) {
        FamilyTree familyTree = getTreeOrThrowException(treeId);

        if (!familyTree.getUser().getUsername().equals(name)) {
            throw new CustomException("Access denied", HttpStatus.FORBIDDEN);
        }

        familyTree.addFamilyMember(familyMember);

        familyMember.setId(null);
        familyMember.setFather(findParent(familyMember.getFather().getId(), treeId, true));
        familyMember.setMother(findParent(familyMember.getMother().getId(), treeId, false));

        // TODO throw exception if familyMember is invalid
        return familyMemberRepository.save(familyMember);
    }

    private FamilyMember findParent(Long parentId, Long treeId, Boolean isFather) {
        String errorMessage = "Invalid " + (isFather ? "father" : "mother") + " id";

        if (parentId == null) {
            return null;
        }

        return familyMemberRepository.findByIdAndFamilyTreeId(parentId, treeId)
                    .orElseThrow(() -> new CustomException(errorMessage, HttpStatus.NOT_FOUND));
    }

    private FamilyTree getTreeOrThrowException(Long treeId) {
        return familyTreeRepository.findById(treeId)
                .orElseThrow(() -> new CustomException("The family tree doesn't exist", HttpStatus.NOT_FOUND));
    }
}
