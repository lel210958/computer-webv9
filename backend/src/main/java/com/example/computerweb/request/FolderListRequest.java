package com.example.computerweb.request;

import com.example.computerweb.model.NetworkLocation;

public class FolderListRequest {
    private NetworkLocation networkLocation;
    private String currentPath;

    public FolderListRequest() {}

    public FolderListRequest(NetworkLocation networkLocation, String currentPath) {
        this.networkLocation = networkLocation;
        this.currentPath = currentPath;
    }

    public NetworkLocation getNetworkLocation() {
        return networkLocation;
    }

    public void setNetworkLocation(NetworkLocation networkLocation) {
        this.networkLocation = networkLocation;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }
} 