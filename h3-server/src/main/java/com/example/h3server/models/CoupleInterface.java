package com.example.h3server.models;

public interface CoupleInterface {

    Long getPrimaryParentId();
    Long getPartnerParentId();
    String getPrimaryParentName();
    String getPartnerParentName();
    Integer getLeftIndex();
    Integer getRightIndex();
    Integer getDepthIndex();
}
