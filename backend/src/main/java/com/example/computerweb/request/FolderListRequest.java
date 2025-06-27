package com.example.computerweb.request;

public class FolderListRequest {
    private Integer networkLocationId;
    private String currentPath;

    public FolderListRequest() {}

    public FolderListRequest(Integer networkLocationId, String currentPath) {
        this.networkLocationId = networkLocationId;
        this.currentPath = currentPath;
    }

    public Integer getNetworkLocationId() {
        return networkLocationId;
    }

    public void setNetworkLocationId(Integer networkLocationId) {
        this.networkLocationId = networkLocationId;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }
} 