package com.example.computerweb.service.impl;

import com.example.computerweb.entity.NetworkLocationEntity;
import com.example.computerweb.mapper.NetworkLocationMapper;
import com.example.computerweb.model.NetworkLocation;
import com.example.computerweb.response.NetworkLocationResponse;
import com.example.computerweb.service.NetworkLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NetworkLocationServiceImpl implements NetworkLocationService {
    
    @Resource
    private NetworkLocationMapper networkLocationMapper;
    
    @Override
    public NetworkLocationResponse getNetworkLocations() {
        // 从数据库查询所有网络位置
        List<NetworkLocationEntity> entities = networkLocationMapper.selectAll();
        
        // 转换为前端需要的格式
        List<NetworkLocation> locations = entities.stream()
            .map(entity -> new NetworkLocation(
                entity.getId(),
                entity.getIp(), 
                entity.getPath(), 
                entity.getUserName(), 
                entity.getPwd()
            ))
            .collect(Collectors.toList());
        
        return new NetworkLocationResponse(locations);
    }
}
