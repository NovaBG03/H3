package com.example.h3server.repositories;

import com.example.h3server.models.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {

    @Query(value = "select * from family_member as fm " +
            "right join " +
            "(select distinct nf.primary_parent_id as id " +
            " from nested_family as nf " +
            " where nf.primary_parent_id <> 0 and nf.family_tree_id = :tree_id " +
            " union " +
            " select distinct nf.partner_parent_id as id " +
            " from nested_family as nf " +
            " where nf.partner_parent_id <> 0 and nf.family_tree_id = :tree_id) as i " +
            "on fm.id = i.id;",
            nativeQuery = true)
    List<FamilyMember> findAllByFamilyTreeId(@Param("tree_id") Long treeId);
}
