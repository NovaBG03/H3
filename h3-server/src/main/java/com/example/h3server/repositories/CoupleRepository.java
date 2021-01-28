package com.example.h3server.repositories;

import com.example.h3server.models.Couple;
import com.example.h3server.models.CoupleId;
import com.example.h3server.models.CoupleInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoupleRepository extends JpaRepository<Couple, CoupleId> {

    // @Query(value = "")
    // void addChildren(@Param("primary_parent_id") int primaryParentId,
    //                @Param("partner_parent_id") int partnerParentId,
    //                @Param("child_id") int childId);

    @Query(value = "select lft from nested_family as nf " +
            "where nf.primary_parent_id = :primary_parent_id " +
            "and nf.partner_parent_id = :partner_parent_id",
            nativeQuery = true)
    int getParentLeftIndex(@Param("primary_parent_id") Long primaryParentId,
                           @Param("partner_parent_id") Long partnerParentId);


    @Modifying
    @Query("update nested_family set rgt = rgt + 2 where rgt > :parentLeft")
    void moveAllRightIndexesAfter(@Param("parentLeft") Integer index);

    @Modifying
    @Query("update nested_family set lft = lft + 2 where lft > :parentLeft")
    void moveAllLeftIndexesAfter(@Param("parentLeft") Integer index);

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
            "on d.primary_parent_id = nf.primary_parent_id AND d.partner_parent_id = nf.partner_parent_id;",
            nativeQuery = true)
    List<CoupleInterface> getAllCouples(@Param("tree_id") Long treeId);

}
