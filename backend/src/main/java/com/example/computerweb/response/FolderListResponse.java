package com.example.computerweb.response;

import com.example.computerweb.entity.FolderItem;

import java.util.List;

public class FolderListResponse {
    private Integer networkLocationId;
    private List<FolderItem> folderList;

    public FolderListResponse() {}

    public FolderListResponse(Integer networkLocationId, List<FolderItem> folderList) {
        this.networkLocationId = networkLocationId;
        this.folderList = folderList;
    }

    public Integer getNetworkLocationId() {
        return networkLocationId;
    }

    public void setNetworkLocationId(Integer networkLocationId) {
        this.networkLocationId = networkLocationId;
    }

    public List<FolderItem> getFolderList() {
        return folderList;
    }

    public void setFolderList(List<FolderItem> folderList) {
        this.folderList = folderList;
    }
} 