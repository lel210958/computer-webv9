package com.example.computerweb.service;

import com.example.computerweb.model.NetworkLocation;

import java.util.Map;

public interface FileScanService {
    
    /**
     * 扫描指定网络位置下的所有文件
     * @param networkLocation 网络位置信息
     * @return 扫描结果
     */
    Map<String, Object> scanNetworkLocation(NetworkLocation networkLocation);
} 