package com.example.h3server.services;

import com.example.h3server.exception.CustomException;
import com.example.h3server.models.Fact;
import com.example.h3server.models.FamilyTree;
import com.example.h3server.models.User;
import com.example.h3server.repositories.FactRepository;
import com.example.h3server.repositories.FamilyTreeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FactService {

    private final FamilyTreeService familyTreeService;
    private final FactRepository factRepository;

    public FactService(FamilyTreeService familyTreeService, FactRepository factRepository) {
        this.familyTreeService = familyTreeService;
        this.factRepository = factRepository;
    }

    public Set<Fact> getFacts(Long treeId, String principalUsername) {
        FamilyTree familyTree = this.familyTreeService.getFamilyTree(treeId, principalUsername);

        return familyTree.getFacts();
    }

    public Set<Fact> getFacts(Long treeId, Long familyMemberId, String principalUsername) {
        FamilyTree familyTree = this.familyTreeService.getFamilyTree(treeId, principalUsername);

        return familyTree.getFacts()
                .stream()
                .filter(f -> f.getFamilyMember().getId().equals(familyMemberId))
                .collect(Collectors.toSet());
    }
}
