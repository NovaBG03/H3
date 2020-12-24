package com.example.h3server.services;

import com.example.h3server.exception.CustomException;
import com.example.h3server.models.Couple;
import com.example.h3server.models.FamilyTree;
import com.example.h3server.repositories.CoupleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoupleService {

    private final CoupleRepository coupleRepository;

    public CoupleService(CoupleRepository coupleRepository) {
        this.coupleRepository = coupleRepository;
    }

    public void addMainMember(FamilyTree familyTree, Long memberId) {

        Couple couple = Couple.builder()
                .primaryParentId(memberId)
                .partnerParentId(0L)
                .leftIndex(1)
                .familyTree(familyTree)
                .build();

        if (familyTree.getCouples().size() == 0) {
            couple.setRightIndex(2);
        } else {
            throw new CustomException("Not Implementedd", HttpStatus.NOT_FOUND);
            // todo update all couples indexes
        }

        coupleRepository.save(couple);
    }

    @Transactional
    public void addChildToCouple(Long primaryParentId, Long partnerParentId, Long childId) {

        final int parentLeftIndex = coupleRepository.getParentLeftIndex(primaryParentId, partnerParentId);

        coupleRepository.moveAllRightIndexesAfter(parentLeftIndex);
        coupleRepository.moveAllLeftIndexesAfter(parentLeftIndex);

        // todo save new couple
        // insert into nested_family (primary_parent_id, partner_parent_id, lft, rgt)
        // values (child_id, 0, @parentLeft + 1, @parentLeft + 2);
    }

    public List<Couple> getAllCouples(FamilyTree familyTree) {
        return this.coupleRepository.getAllCouples(familyTree.getId())
                .stream()
                .map(coupleInterface -> Couple.builder()
                        .primaryParentId(coupleInterface.getPrimaryParentId())
                        .partnerParentId(coupleInterface.getPartnerParentId())
                        .primaryParentName(coupleInterface.getPrimaryParentName())
                        .partnerParentName(coupleInterface.getPartnerParentName())
                        .leftIndex(coupleInterface.getLeftIndex())
                        .rightIndex(coupleInterface.getRightIndex())
                        .familyTree(familyTree)
                        .depthIndex(coupleInterface.getDepthIndex())
                        .build())
                .collect(Collectors.toList());
    }
}
