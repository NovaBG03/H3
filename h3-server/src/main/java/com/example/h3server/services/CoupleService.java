package com.example.h3server.services;

import com.example.h3server.exception.CustomException;
import com.example.h3server.models.Couple;
import com.example.h3server.models.FamilyTree;
import com.example.h3server.repositories.CoupleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
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
    public void addChildWithPartner(FamilyTree familyTree, Long primaryParentId, Long partnerParentId,
                                    Long childId, Long childPartnerId) {
        final int parentLeftIndex = coupleRepository
                .getParentLeftIndex(familyTree.getId(), primaryParentId, partnerParentId);

        coupleRepository.moveForwardAllRightIndexesAfter(familyTree.getId(), parentLeftIndex);
        coupleRepository.moveForwardAllLeftIndexesAfter(familyTree.getId(), parentLeftIndex);


        coupleRepository.save(Couple.builder()
                .primaryParentId(childId)
                .partnerParentId(childPartnerId)
                .leftIndex(parentLeftIndex + 1)
                .rightIndex(parentLeftIndex + 2)
                .familyTree(familyTree)
                .build());
    }


    @Transactional
    public void addChild(FamilyTree familyTree, Long primaryParentId, Long partnerParentId, Long childId) {

        if (primaryParentId == 0 && partnerParentId == 0) {
            final int rightmostId = coupleRepository.getBiggestRightIndex(familyTree.getId());

            coupleRepository.save(Couple.builder()
                    .primaryParentId(childId)
                    .partnerParentId(0L)
                    .leftIndex(rightmostId + 1)
                    .rightIndex(rightmostId + 2)
                    .familyTree(familyTree)
                    .build());
        } else {
            this.addChildWithPartner(familyTree, primaryParentId, partnerParentId, childId, 0L);
        }
    }

    public List<Couple> getAllCouples(FamilyTree familyTree) {
        List<Couple> couples = this.coupleRepository.getAllCouples(familyTree.getId())
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
        return couples;
    }

    public void addPartner(FamilyTree familyTree, Long primaryParentId, Long partnerParentId) {
        List<Couple> couples = coupleRepository.findByPrimaryParentId(familyTree.getId(), primaryParentId);

        if (couples.isEmpty()) {
            throw new CustomException("Invalid Primary Parent Id", HttpStatus.NOT_FOUND);
        }

        Couple vacantCouple = couples.stream()
                .filter(c -> c.getPartnerParentId().equals(0L))
                .findFirst()
                .orElse(null);

        if (vacantCouple != null) {
            coupleRepository.changePartnerParentId(
                    familyTree.getId(),
                    primaryParentId,
                    0L,
                    partnerParentId);
        } else {
            // there is no empty couple and should be added new one
            Couple childCouple = couples.stream().findFirst().get();
            Couple parentCouple = coupleRepository
                    .findParentCouple(familyTree.getId(), childCouple.getLeftIndex(), childCouple.getRightIndex());

            if (parentCouple == null) {
                // should add new main member
                log.warn("Should add new Main member!");
                return;
            }

            this.addChildWithPartner(familyTree,
                    parentCouple.getPrimaryParentId(),
                    parentCouple.getPartnerParentId(),
                    primaryParentId,
                    partnerParentId);
        }
    }

    public Collection<Long> deleteMember(FamilyTree familyTree, Long memberId) {
        Set<Long> membersToDelete = new HashSet<>();

        List<Couple> couples = this.getAllCouples(familyTree);

        List<Couple> primaryCouples = couples
                .stream()
                .filter(couple -> couple.getPrimaryParentId().equals(memberId))
                .collect(Collectors.toList());
        for (Couple couple : primaryCouples) {
            membersToDelete.addAll(this.deletePrimaryParent(familyTree, couple, couples));
        }


        List<Couple> partnerCouples = couples
                .stream()
                .filter(couple -> couple.getPartnerParentId().equals(memberId))
                .collect(Collectors.toList());
        for (Couple couple : partnerCouples) {
            membersToDelete.addAll(this.deletePartnerParent(familyTree, couple, couples));
        }

        return membersToDelete.stream().filter(id -> id != 0).collect(Collectors.toList());
    }

    private Set<Long> deletePrimaryParent(FamilyTree familyTree, Couple couple, List<Couple> allCouples) {
        Set<Long> membersToDelete = new HashSet<>();

        if (couple.hasChildren()) {
            List<Couple> childCouples = allCouples.stream()
                    .filter(c -> c.getLeftIndex() > couple.getLeftIndex()
                            && c.getRightIndex() < couple.getRightIndex()
                            && c.getDepthIndex().equals(couple.getDepthIndex() + 1))
                    .collect(Collectors.toList());

            for (Couple childCouple : childCouples) {
                membersToDelete.addAll(this.deletePrimaryParent(familyTree, childCouple, allCouples));
            }
        }

        Integer leftIndex = couple.getLeftIndex();
        membersToDelete.add(couple.getPrimaryParentId());
        membersToDelete.add(couple.getPartnerParentId());

        this.coupleRepository.delete(couple);

        coupleRepository.moveBackAllRightIndexesAfter(familyTree.getId(), leftIndex);
        coupleRepository.moveBackAllLeftIndexesAfter(familyTree.getId(), leftIndex);

        return membersToDelete;
    }

    private Set<Long> deletePartnerParent(FamilyTree familyTree, Couple couple, List<Couple> allCouples) {
        Set<Long> membersToDelete = new HashSet<>();

        if (couple.hasChildren()) {
            List<Couple> childCouples = allCouples.stream()
                    .filter(c -> c.getLeftIndex() > couple.getLeftIndex()
                            && c.getRightIndex() < couple.getRightIndex()
                            && c.getDepthIndex().equals(couple.getDepthIndex() + 1))
                    .collect(Collectors.toList());

            for (Couple childCouple : childCouples) {
                membersToDelete.addAll(this.deletePrimaryParent(familyTree, childCouple, allCouples));
            }
        }

        membersToDelete.add(couple.getPartnerParentId());

        Long primaryParentId = couple.getPrimaryParentId();
        Integer leftIndex = couple.getLeftIndex();

        this.coupleRepository.delete(couple);

        if (coupleRepository.findByPrimaryParentId(familyTree.getId(), primaryParentId).isEmpty()) {
            couple.setPartnerParentId(0L);
            couple.setRightIndex(couple.getLeftIndex() + 1);
            this.coupleRepository.save(couple);
        } else {
            coupleRepository.moveBackAllRightIndexesAfter(familyTree.getId(), leftIndex);
            coupleRepository.moveBackAllLeftIndexesAfter(familyTree.getId(), leftIndex);
        }

        membersToDelete.remove(primaryParentId);

        return membersToDelete;
    }
}
