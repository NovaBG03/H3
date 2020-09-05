package com.example.h3server.services;

import com.example.h3server.models.FamilyTree;
import com.example.h3server.repositories.FamilyTreeRepository;
import com.example.h3server.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FamilyTreeService {

    private final FamilyTreeRepository familyTreeRepository;
    private final UserRepository userRepository;

    public FamilyTreeService(FamilyTreeRepository familyTreeRepository, UserRepository userRepository) {
        this.familyTreeRepository = familyTreeRepository;
        this.userRepository = userRepository;
    }

    public List<FamilyTree> getFamilyTrees(String username) {
        return this.familyTreeRepository.findAllByUser(userRepository.findByUsername(username));
    }
}
