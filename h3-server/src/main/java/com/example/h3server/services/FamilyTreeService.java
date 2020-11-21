package com.example.h3server.services;

import com.example.h3server.exception.CustomException;
import com.example.h3server.models.FamilyTree;
import com.example.h3server.models.TreeTag;
import com.example.h3server.models.User;
import com.example.h3server.repositories.FamilyTreeRepository;
import com.example.h3server.repositories.UserRepository;
import com.example.h3server.utils.ModelValidator;
import com.google.common.collect.Sets;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FamilyTreeService {

    private final FamilyTreeRepository familyTreeRepository;
    private final UserRepository userRepository;
    private final TreeTagService treeTagService;

    public FamilyTreeService(FamilyTreeRepository familyTreeRepository,
                             UserRepository userRepository,
                             TreeTagService treeTagService) {
        this.familyTreeRepository = familyTreeRepository;
        this.userRepository = userRepository;
        this.treeTagService = treeTagService;
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

    public FamilyTree getFamilyTree(Long id, String principalUsername) {
        FamilyTree familyTree = this.familyTreeRepository.findById(id)
                .orElseThrow(() -> new CustomException("The family tree doesn't exist", HttpStatus.NOT_FOUND));

        User principal = userRepository.findByUsername(principalUsername);

        if (familyTree.getIsPrivate()
                && !familyTree.getUser().getUsername().equals(principalUsername)
                && !principal.isAdmin()) {
            throw new CustomException("The family tree doesn't exist", HttpStatus.NOT_FOUND);
        }

        return familyTree;
    }

    public FamilyTree createNewFamilyTree(FamilyTree familyTree, String principalUsername) {
        familyTree.setId(null);

        User user = this.userRepository.findByUsername(principalUsername);
        user.addFamilyTree(familyTree);

        familyTree.setCreatedAt(LocalDateTime.now());
        familyTree.setTags(Sets.newHashSet());

        ModelValidator.validate(familyTree);
        FamilyTree savedFamilyTree = this.familyTreeRepository.save(familyTree);
        return savedFamilyTree;
    }

    public FamilyTree updateFamilyTree(Long id, FamilyTree familyTree, String principalUsername) {
        FamilyTree treeFromDb = getFamilyTreeOrThrowException(id, principalUsername);

        treeFromDb.setName(familyTree.getName());
        treeFromDb.setIsPrivate(familyTree.getIsPrivate());

        ModelValidator.validate(treeFromDb);
        Collection<TreeTag> tagsToDelete = Sets.newHashSet(treeFromDb.getTags());

        treeFromDb.setTags(familyTree.getTags()
                .stream()
                .map(treeTag -> treeTagService.getOrCreateTreeTag(treeTag.getLabel()))
                .collect(Collectors.toSet()));

        treeFromDb.getTags().forEach(tag -> tagsToDelete.remove(tag));

        FamilyTree savedTree = this.familyTreeRepository.save(treeFromDb);

        tagsToDelete.forEach(tagToDelete -> treeTagService.deleteUnnecessaryTreeTag(tagToDelete));

        return savedTree;
    }

    public void deleteFamilyTree(Long id, String principalUsername) {
        FamilyTree treeFromDb = getFamilyTreeOrThrowException(id, principalUsername);
        this.familyTreeRepository.delete(treeFromDb);
    }

    private FamilyTree getFamilyTreeOrThrowException(Long id, String principalUsername) {
        FamilyTree treeFromDb = this.familyTreeRepository.findByIdAndUserUsername(id, principalUsername);

        if (treeFromDb == null) {
            throw new CustomException("The family tree doesn't exist", HttpStatus.NOT_FOUND);
        }

        return treeFromDb;
    }

    public List<FamilyTree> findFamilyTrees(String treePattern, String principalUsername) {
        final User user = userRepository.findByUsername(principalUsername);

        List<FamilyTree> familyTrees = this.familyTreeRepository
                .findAllByNameContainingIgnoreCase(treePattern.trim());

        if (user.isAdmin()) {
            return familyTrees;
        }

        return familyTrees
                .stream()
                .filter(familyTree -> !familyTree.getIsPrivate())
                .collect(Collectors.toList());
    }
}
