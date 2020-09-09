package com.example.h3server.repositories;

import com.example.h3server.models.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {

    List<FamilyMember> findAllByFamilyTreeId(Long id);

    Optional<FamilyMember> findByIdAndFamilyTreeId(Long memberId, Long treeId);
}
