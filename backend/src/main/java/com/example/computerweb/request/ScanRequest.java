package com.example.computerweb.request;

public class ScanRequest {
    private Integer networkLocationId;

    public ScanRequest() {}

    public ScanRequest(Integer networkLocationId) {
        this.networkLocationId = networkLocationId;
    }

    public Integer getNetworkLocationId() {
        return networkLocationId;
    }

    public void setNetworkLocationId(Integer networkLocationId) {
        this.networkLocationId = networkLocationId;
    }
} 