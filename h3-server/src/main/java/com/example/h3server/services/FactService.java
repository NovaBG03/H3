package com.example.h3server.services;

import com.example.h3server.exception.CustomException;
import com.example.h3server.models.Fact;
import com.example.h3server.models.FamilyMember;
import com.example.h3server.models.FamilyTree;
import com.example.h3server.repositories.FactRepository;
import com.example.h3server.repositories.FamilyMemberRepository;
import com.example.h3server.utils.ModelValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FactService {

    private final FamilyTreeService familyTreeService;
    private final FamilyMemberRepository familyMemberRepository;
    private final FactRepository factRepository;

    public FactService(FamilyTreeService familyTreeService,
                       FamilyMemberRepository familyMemberRepository,
                       FactRepository factRepository) {
        this.familyTreeService = familyTreeService;
        this.familyMemberRepository = familyMemberRepository;
        this.factRepository = factRepository;
    }

    public Set<Fact> getFacts(Long treeId, String principalUsername) {
        FamilyTree familyTree = this.familyTreeService.getFamilyTree(treeId, principalUsername);

        return familyTree.getFacts();
    }

    public Set<Fact> getFacts(Long treeId, Long familyMemberId, String principalUsername) {
        // TODO make get fact by memberId endpoint
        FamilyTree familyTree = this.familyTreeService.getFamilyTree(treeId, principalUsername);

        return familyTree.getFacts()
                .stream()
                .filter(f -> f.getFamilyMember().getId().equals(familyMemberId))
                .collect(Collectors.toSet());
    }

    public Fact createFact(Long treeId, Long familyMemberId, Fact fact, String principalUsername) {
        return this.updateFact(treeId, 0L, familyMemberId, fact, principalUsername);
    }

    public Fact updateFact(Long treeId, Long factId, Long familyMemberId, Fact fact, String principalUsername) {
        FamilyTree familyTree = this.familyTreeService.getFamilyTree(treeId, principalUsername);

        FamilyMember familyMember = null;
        try {
            familyMember = this.familyMemberRepository.findByIdAndFamilyTreeId(familyMemberId, treeId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (familyMember == null) {
            throw new CustomException("Invalid Family Member Id", HttpStatus.NOT_FOUND);
        }

        fact.setId(factId);
        ModelValidator.validate(fact);

        fact.setFamilyMember(familyMember);
        fact.setFamilyTree(familyTree);

        return this.factRepository.save(fact);
    }

    public void deleteFact(Long treeId, Long factId, String principalUsername) {
        this.familyTreeService.getFamilyTree(treeId, principalUsername); // check for permissions
        try {
            this.factRepository.deleteById(factId);
        }
        catch (Exception ex) {
            throw new CustomException("Fact does not exists", HttpStatus.NOT_FOUND);
        }
    }
}
