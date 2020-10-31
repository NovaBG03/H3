package com.example.h3server.repositories;

import com.example.h3server.models.FamilyTree;
import com.example.h3server.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FamilyTreeRepository extends JpaRepository<FamilyTree, Long> {

    List<FamilyTree> findAllByUser(User user);

    List<FamilyTree> findAllByNameContainingIgnoreCase(String treeName);

    FamilyTree findByIdAndUserUsername(Long id, String username);
}
