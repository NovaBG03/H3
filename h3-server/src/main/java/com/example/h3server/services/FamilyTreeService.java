package com.example.h3server.services;

import com.example.h3server.exception.CustomException;
import com.example.h3server.models.FamilyTree;
import com.example.h3server.models.User;
import com.example.h3server.repositories.FamilyTreeRepository;
import com.example.h3server.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FamilyTreeService {

    private final FamilyTreeRepository familyTreeRepository;
    private final UserRepository userRepository;

    public FamilyTreeService(FamilyTreeRepository familyTreeRepository, UserRepository userRepository) {
        this.familyTreeRepository = familyTreeRepository;
        this.userRepository = userRepository;
    }

    public List<FamilyTree> getFamilyTrees(String username, String principalUsername) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
        }

        List<FamilyTree> familyTrees = this.familyTreeRepository.findAllByUser(user);

        final boolean isOwner = username.equals(principalUsername);
        if (isOwner) {
            return familyTrees;
        }

        return familyTrees.stream().filter(familyTree -> !familyTree.getIsPrivate()).collect(Collectors.toList());
    }

    public FamilyTree createNewFamilyTree(FamilyTree familyTree, String principalUsername) {
        familyTree.setId(null);

        User user = this.userRepository.findByUsername(principalUsername);
        familyTree.setUser(user);

        familyTree.setCreatedAt(LocalDateTime.now());

        FamilyTree savedFamilyTree = this.familyTreeRepository.save(familyTree);
        return savedFamilyTree;
    }

    public FamilyTree updateFamilyTree(Long id, FamilyTree familyTree, String principalUsername) {
        FamilyTree treeFromDb = this.familyTreeRepository.findByIdAndUserUsername(id, principalUsername);

        if (treeFromDb == null) {
            throw new CustomException("The family tree doesn't exist", HttpStatus.NOT_FOUND);
        }

        treeFromDb.setName(familyTree.getName());
        treeFromDb.setIsPrivate(familyTree.getIsPrivate());

        FamilyTree savedTree = this.familyTreeRepository.save(treeFromDb);

        return savedTree;
    }
}
