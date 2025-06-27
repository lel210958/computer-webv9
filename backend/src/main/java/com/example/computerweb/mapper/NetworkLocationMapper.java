package com.example.computerweb.mapper;

import com.example.computerweb.entity.NetworkLocationEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NetworkLocationMapper {
    
    /**
     * 查询所有网络位置
     */
    List<NetworkLocationEntity> selectAll();
    
    /**
     * 根据ID查询网络位置
     */
    NetworkLocationEntity selectById(Integer id);
    
    /**
     * 插入网络位置
     */
    int insert(NetworkLocationEntity networkLocation);
    
    /**
     * 更新网络位置
     */
    int update(NetworkLocationEntity networkLocation);
    
    /**
     * 删除网络位置
     */
    int deleteById(Integer id);
} 