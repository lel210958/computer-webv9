package com.example.computerweb.request;

public class CategoryCountRequest {
    private Integer networkLocationId;

    public CategoryCountRequest() {}

    public CategoryCountRequest(Integer networkLocationId) {
        this.networkLocationId = networkLocationId;
    }

    public Integer getNetworkLocationId() {
        return networkLocationId;
    }

    public void setNetworkLocationId(Integer networkLocationId) {
        this.networkLocationId = networkLocationId;
    }
} 