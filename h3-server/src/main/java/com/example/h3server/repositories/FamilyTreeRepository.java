package com.example.h3server.repositories;

import com.example.h3server.models.FamilyTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyTreeRepository extends JpaRepository<FamilyTree, Long> {
}
