package com.example.computerweb.request;

import com.example.computerweb.model.NetworkLocation;

public class CategoryCountRequest {
    private NetworkLocation networkLocation;

    public CategoryCountRequest() {}

    public CategoryCountRequest(NetworkLocation networkLocation) {
        this.networkLocation = networkLocation;
    }

    public NetworkLocation getNetworkLocation() {
        return networkLocation;
    }

    public void setNetworkLocation(NetworkLocation networkLocation) {
        this.networkLocation = networkLocation;
    }
} 