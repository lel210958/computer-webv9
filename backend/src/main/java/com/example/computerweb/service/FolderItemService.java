package com.example.computerweb.service;

import com.example.computerweb.entity.FolderItem;
import com.example.computerweb.model.CategoryCount;
import com.example.computerweb.request.FolderListRequest;
import com.example.computerweb.response.FolderListResponse;

import java.util.List;

public interface FolderItemService {

    /**
     * 根据网络共享位置ID和当前路径查询下面子目录和文件
     * @param request 网络共享位置ID和当前路径
     */
    FolderListResponse selectByLocationAndParentPath(FolderListRequest request);
    
    /**
     * 查询指定网络位置下的分类统计
     * @param networkLocationId 网络位置ID
     * @return 分类统计列表
     */
    List<CategoryCount> selectCategoryCount(Integer networkLocationId);
    
    /**
     * 查询指定网络位置和分类类型下的所有文件
     * @param networkLocationId 网络位置ID
     * @param fileType 文件类型
     * @return 文件列表
     */
    List<FolderItem> selectByLocationAndCategory(Integer networkLocationId, String fileType);

    /**
     * 分页+模糊搜索分类文件
     */
    FolderListResponse selectByLocationAndCategoryPaged(Integer networkLocationId, String fileType, String fileName, Integer pageNum, Integer pageSize);
}
