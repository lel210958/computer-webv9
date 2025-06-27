package com.example.computerweb.request;

public class AddNetworkLocationRequest {
    private String ip;
    private String path;
    private String userName;
    private String pwd;

    public AddNetworkLocationRequest() {}

    public AddNetworkLocationRequest(String ip, String path, String userName, String pwd) {
        this.ip = ip;
        this.path = path;
        this.userName = userName;
        this.pwd = pwd != null ? pwd : "";
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd != null ? pwd : "";
    }
} 