package com.example.computerweb.mapper;

import com.example.computerweb.entity.FolderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FolderItemMapper {
    // 查询指定网络位置和父目录下的所有文件/文件夹
    List<FolderItem> selectByLocationAndParentPath(@Param("networkShareLocation") String networkShareLocation, @Param("parentPath") String parentPath);
}
