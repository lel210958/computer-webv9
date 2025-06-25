package com.example.computerweb.service;

import com.example.computerweb.entity.FolderItem;
import com.example.computerweb.request.FolderListRequest;
import com.example.computerweb.response.FolderListResponse;

import java.util.List;

public interface FolderItemService {

    /**
     * 根据网络共享位置和当前路径查询下面子目录和文件
     * @param request 网络共享位置和当前路径
     */
    FolderListResponse selectByLocationAndParentPath(FolderListRequest request);
}
