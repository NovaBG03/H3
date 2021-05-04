package com.example.h3server.repositories;

import com.example.h3server.models.Couple;
import com.example.h3server.models.CoupleId;
import com.example.h3server.models.CoupleInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoupleRepository extends JpaRepository<Couple, CoupleId> {

    @Query(value = "select lft from nested_family as nf " +
            "where nf.primary_parent_id = :primary_parent_id " +
            "and nf.partner_parent_id = :partner_parent_id " +
            "and nf.family_tree_id = :tree_id",
            nativeQuery = true)
    int getParentLeftIndex(@Param("tree_id") Long treeId,
                           @Param("primary_parent_id") Long primaryParentId,
                           @Param("partner_parent_id") Long partnerParentId);

    @Modifying
    @Query("update nested_family set rgt = rgt + 2 where rgt > :parentLeft and family_tree_id = :tree_id")
    void moveForwardAllRightIndexesAfter(@Param("tree_id") Long treeId, @Param("parentLeft") Integer index);

    @Modifying
    @Query("update nested_family set lft = lft + 2 where lft > :parentLeft and family_tree_id = :tree_id")
    void moveForwardAllLeftIndexesAfter(@Param("tree_id") Long treeId, @Param("parentLeft") Integer index);

    @Modifying
    @Query("update nested_family set rgt = rgt - 2 where rgt > :parentLeft and family_tree_id = :tree_id")
    void moveBackAllRightIndexesAfter(@Param("tree_id") Long treeId, @Param("parentLeft") Integer index);

    @Modifying
    @Query("update nested_family set lft = lft - 2 where lft > :parentLeft and family_tree_id = :tree_id")
    void moveBackAllLeftIndexesAfter(@Param("tree_id") Long treeId, @Param("parentLeft") Integer index);

    @Query(value = "select nf.primary_parent_id as primaryParentId, " +
            "nf.partner_parent_id as partnerParentId, " +
            "concat(m.first_name, ' ', m.last_name) as primaryParentName, " +
            "concat(pm.first_name, ' ', pm.last_name) as partnerParentName, " +
            "nf.lft as LeftIndex, " +
            "nf.rgt as RightIndex , " +
            "d.depth as DepthIndex " +
            "from nested_family as nf " +
            "join family_member as m on nf.primary_parent_id = m.id " +
            "left join family_member as pm on nf.partner_parent_id = pm.id " +
            "join " +
            " (SELECT node.primary_parent_id, " +
            " node.partner_parent_id, " +
            " COUNT(CONCAT(node.primary_parent_id, node.partner_parent_id)) - 1 AS depth " +
            " FROM nested_family AS node, " +
            " nested_family AS parent " +
            " WHERE node.family_tree_id = :tree_id AND node.lft BETWEEN parent.lft AND parent.rgt " +
            " GROUP BY node.primary_parent_id, node.partner_parent_id " +
            " ORDER BY node.lft) as d " +
            "on d.primary_parent_id = nf.primary_parent_id AND d.partner_parent_id = nf.partner_parent_id",
            nativeQuery = true)
    List<CoupleInterface> getAllCouples(@Param("tree_id") Long treeId);

    @Query(value = "SELECT max(nf.rgt) FROM NESTED_FAMILY as nf where nf.family_tree_id = :tree_id",
            nativeQuery = true)
    int getBiggestRightIndex(@Param("tree_id") Long treeId);

    @Query(value = "select * from nested_family as nf " +
            "where nf.primary_parent_id = :primary_parent_id and nf.family_tree_id = :tree_id",
            nativeQuery = true)
    List<Couple> findByPrimaryParentId(@Param("tree_id") Long treeId,
                                       @Param("primary_parent_id") Long primaryParentId);

    @Modifying
    @Query("update nested_family set partner_parent_id = :new_partner_id " +
            "where primary_parent_id = :primary_parent_id " +
            "and partner_parent_id = :initial_partner_id " +
            "and family_tree_id = :tree_id")
    void changePartnerParentId(@Param("tree_id") Long id,
                               @Param("primary_parent_id") Long primaryParentId,
                               @Param("initial_partner_id") Long initialPartnerId,
                               @Param("new_partner_id") Long newPartnerId);

    @Query(value = "select * from nested_family as nf where nf.lft = " +
            "(select max(nf.lft) from nested_family as nf where nf.lft < :child_lft and nf.rgt > :child_rgt) " +
            "and nf.family_tree_id = :tree_id",
            nativeQuery = true)
    Couple findParentCouple(@Param("tree_id") Long id,
                            @Param("child_lft") Integer leftIndex,
                            @Param("child_rgt") Integer rightIndex);

}
