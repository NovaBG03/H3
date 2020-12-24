package com.example.h3server.models;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class CoupleId implements Serializable {

    private Long primaryParentId;
    private Long partnerParentId;

    @Builder
    public CoupleId(Long primaryParentId, Long partnerParentId) {
        this.primaryParentId = primaryParentId;
        this.partnerParentId = partnerParentId;
    }
}
