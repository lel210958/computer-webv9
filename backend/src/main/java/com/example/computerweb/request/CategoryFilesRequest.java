package com.example.computerweb.request;

import com.example.computerweb.model.NetworkLocation;

public class CategoryFilesRequest {
    private NetworkLocation networkLocation;
    private String type;

    public CategoryFilesRequest() {}

    public CategoryFilesRequest(NetworkLocation networkLocation, String type) {
        this.networkLocation = networkLocation;
        this.type = type;
    }

    public NetworkLocation getNetworkLocation() {
        return networkLocation;
    }

    public void setNetworkLocation(NetworkLocation networkLocation) {
        this.networkLocation = networkLocation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
} 