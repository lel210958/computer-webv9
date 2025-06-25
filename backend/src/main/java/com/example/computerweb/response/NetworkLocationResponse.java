package com.example.computerweb.response;

import com.example.computerweb.model.NetworkLocation;

import java.util.List;

public class NetworkLocationResponse {
    private List<NetworkLocation> networkLocationList;

    public NetworkLocationResponse() {}

    public NetworkLocationResponse(List<NetworkLocation> networkLocationList) {
        this.networkLocationList = networkLocationList;
    }

    public List<NetworkLocation> getNetworkLocationList() {
        return networkLocationList;
    }

    public void setNetworkLocationList(List<NetworkLocation> networkLocationList) {
        this.networkLocationList = networkLocationList;
    }
} 