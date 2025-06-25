package com.example.computerweb.model;

public class NetworkLocation {
    private String ip;
    private String name;
    private String user;
    private String pwd;

    public NetworkLocation() {}

    public NetworkLocation(String ip, String name, String user, String pwd) {
        this.ip = ip;
        this.name = name;
        this.user = user;
        this.pwd = pwd;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
} 