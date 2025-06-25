package com.example.computerweb.response;

import com.example.computerweb.entity.FolderItem;
import com.example.computerweb.model.NetworkLocation;

import java.util.List;

public class FolderListResponse {
    private NetworkLocation networkLocation;
    private List<FolderItem> folderList;

    public FolderListResponse() {}

    public FolderListResponse(NetworkLocation networkLocation, List<FolderItem> folderList) {
        this.networkLocation = networkLocation;
        this.folderList = folderList;
    }

    public NetworkLocation getNetworkLocation() {
        return networkLocation;
    }

    public void setNetworkLocation(NetworkLocation networkLocation) {
        this.networkLocation = networkLocation;
    }

    public List<FolderItem> getFolderList() {
        return folderList;
    }

    public void setFolderList(List<FolderItem> folderList) {
        this.folderList = folderList;
    }
} 