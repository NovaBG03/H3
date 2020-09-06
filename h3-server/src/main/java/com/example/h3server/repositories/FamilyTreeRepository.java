package com.example.h3server.repositories;

import com.example.h3server.models.FamilyTree;
import com.example.h3server.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyTreeRepository extends JpaRepository<FamilyTree, Long> {

    List<FamilyTree> findAllByUser(User user);

    FamilyTree findByIdAndUserUsername(Long id, String username);
}
