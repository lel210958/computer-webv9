package com.example.computerweb.request;

public class CategoryFilesRequest {
    private Integer networkLocationId;
    private String type;
    private String fileName;
    private Integer pageNum;
    private Integer pageSize;

    public CategoryFilesRequest() {}

    public CategoryFilesRequest(Integer networkLocationId, String type) {
        this.networkLocationId = networkLocationId;
        this.type = type;
    }

    public Integer getNetworkLocationId() {
        return networkLocationId;
    }

    public void setNetworkLocationId(Integer networkLocationId) {
        this.networkLocationId = networkLocationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
} 