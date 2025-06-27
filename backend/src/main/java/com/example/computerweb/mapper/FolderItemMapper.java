package com.example.computerweb.mapper;

import com.example.computerweb.entity.FolderItem;
import com.example.computerweb.model.CategoryCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FolderItemMapper {
    // 查询指定网络位置和父目录下的所有文件/文件夹
    List<FolderItem> selectByLocationAndParentPath(@Param("networkLocationId") Integer networkLocationId, @Param("parentPath") String parentPath);
    
    // 根据网络位置和文件路径查询单个文件记录
    FolderItem selectByLocationAndFilePath(@Param("networkLocationId") Integer networkLocationId, @Param("filePath") String filePath);
    
    // 插入新的文件记录
    int insertFolderItem(FolderItem folderItem);
    
    // 更新文件记录
    int updateFolderItem(FolderItem folderItem);
    
    // 删除指定网络位置下的所有记录
    int deleteByNetworkLocation(@Param("networkLocationId") Integer networkLocationId);
    
    // 查询指定网络位置下的所有记录
    List<FolderItem> selectByNetworkLocation(@Param("networkLocationId") Integer networkLocationId);
    
    // 查询指定网络位置下的分类统计
    List<CategoryCount> selectCategoryCount(@Param("networkLocationId") Integer networkLocationId);
    
    // 查询指定网络位置和分类类型下的所有文件
    List<FolderItem> selectByLocationAndCategory(@Param("networkLocationId") Integer networkLocationId, @Param("fileType") String fileType);
    
    // 分页+模糊搜索分类文件
    List<FolderItem> selectByLocationAndCategoryPaged(
        @Param("networkLocationId") Integer networkLocationId,
        @Param("fileType") String fileType,
        @Param("fileName") String fileName,
        @Param("offset") Integer offset,
        @Param("limit") Integer limit
    );
    
    // 查询总数
    int countByLocationAndCategoryPaged(
        @Param("networkLocationId") Integer networkLocationId,
        @Param("fileType") String fileType,
        @Param("fileName") String fileName
    );
}
