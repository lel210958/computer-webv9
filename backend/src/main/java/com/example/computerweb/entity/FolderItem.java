package com.example.computerweb.entity;

import java.util.Date;
import org.apache.ibatis.type.Alias;

@Alias("FolderItem")
public class FolderItem {
    private Integer id;
    private String fileName;
    private String filePath;
    private String parentPath;
    private Long fileSize;
    private String fileType;
    private String networkShareLocation;
    private Date lastModify;
    private Date syncTime;
    private Date statisDate;
    private Integer isDirectory;

    public FolderItem() {}

    public FolderItem(String fileName, String isDirectory, String fileSize, String fileType, String filePath, String lastModify) {
        this.fileName = fileName;
        this.isDirectory = Integer.parseInt(isDirectory);
        this.fileSize = Long.parseLong(fileSize);
        this.fileType = fileType;
        this.filePath = filePath;
        this.lastModify = new Date(Long.parseLong(lastModify));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getNetworkShareLocation() {
        return networkShareLocation;
    }

    public void setNetworkShareLocation(String networkShareLocation) {
        this.networkShareLocation = networkShareLocation;
    }

    public Date getLastModify() {
        return lastModify;
    }

    public void setLastModify(Date lastModify) {
        this.lastModify = lastModify;
    }

    public Date getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
    }

    public Date getStatisDate() {
        return statisDate;
    }

    public void setStatisDate(Date statisDate) {
        this.statisDate = statisDate;
    }

    public Integer getIsDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(Integer isDirectory) {
        this.isDirectory = isDirectory;
    }
} 