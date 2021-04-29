package com.example.h3server.models;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity(name = "nested_family")
@IdClass(CoupleId.class)
@Getter
@Setter
@NoArgsConstructor
public class Couple {

    @Id
    private Long primaryParentId;

    @Id
    private Long partnerParentId;

    @ManyToOne
    private FamilyTree familyTree;

    @NotNull(message = "Left index can't be null")
    @Column(name = "lft", nullable = false)
    private Integer leftIndex;

    @NotNull(message = "Right index can't be null")
    @Column(name = "rgt", nullable = false)
    private Integer rightIndex;

    @Transient
    private String primaryParentName;

    @Transient
    private String partnerParentName;

    @Transient
    private Integer depthIndex;

    @Builder
    public Couple(Long primaryParentId,
                  Long partnerParentId,
                  String primaryParentName,
                  String partnerParentName,
                  FamilyTree familyTree,
                  Integer leftIndex,
                  Integer rightIndex,
                  Integer depthIndex) {
        this.primaryParentId = primaryParentId;
        this.partnerParentId = partnerParentId;
        this.primaryParentName = primaryParentName;
        this.partnerParentName = partnerParentName;
        this.familyTree = familyTree;
        this.leftIndex = leftIndex;
        this.rightIndex = rightIndex;
        this.depthIndex = depthIndex;
    }

    public boolean hasChildren() {
        return this.getRightIndex() - this.getLeftIndex() != 1;
    }
}
