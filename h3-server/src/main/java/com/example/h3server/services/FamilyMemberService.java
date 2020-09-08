package com.example.h3server.services;

import com.example.h3server.exception.CustomException;
import com.example.h3server.models.FamilyMember;
import com.example.h3server.models.FamilyTree;
import com.example.h3server.repositories.FamilyMemberRepository;
import com.example.h3server.repositories.FamilyTreeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        CustomException treeNotFoundException =
                new CustomException("The family tree doesn't exist", HttpStatus.NOT_FOUND);

        FamilyTree familyTree = familyTreeRepository.findById(treeId)
                .orElseThrow(() -> treeNotFoundException);

        if (familyTree.getIsPrivate() && !familyTree.getUser().getUsername().equals(username)) {
            throw treeNotFoundException;
        }

        return familyMemberRepository.findAllByFamilyTreeId(treeId);
    }
}
