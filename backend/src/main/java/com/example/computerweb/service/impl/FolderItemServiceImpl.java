package com.example.computerweb.service.impl;

import com.example.computerweb.entity.FolderItem;
import com.example.computerweb.mapper.FolderItemMapper;
import com.example.computerweb.model.CategoryCount;
import com.example.computerweb.request.FolderListRequest;
import com.example.computerweb.response.FolderListResponse;
import com.example.computerweb.service.FolderItemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FolderItemServiceImpl implements FolderItemService {

    @Resource
    private FolderItemMapper folderItemMapper;

    @Override
    public FolderListResponse selectByLocationAndParentPath(FolderListRequest request) {
        String currentPath = request.getCurrentPath();
        Integer networkLocationId = request.getNetworkLocationId();
        
        if (networkLocationId == null) {
            // 如果网络位置ID为空，返回空列表
            return new FolderListResponse(networkLocationId, Collections.emptyList());
        }
        
        // 查询数据库
        List<FolderItem> folderList = folderItemMapper.selectByLocationAndParentPath(
            networkLocationId, 
            currentPath == null ? "" : currentPath
        );
        
        // 兼容前端需要的部分字段格式
        List<FolderItem> result = folderList.stream().map(item -> {
            FolderItem f = new FolderItem();
            f.setFileName(item.getFileName());
            f.setIsDirectory(item.getIsDirectory());
            f.setFileSize(item.getFileSize());
            f.setFileType(item.getFileType());
            f.setFilePath(item.getFilePath());
            f.setLastModify(item.getLastModify());
            return f;
        }).collect(Collectors.toList());
        
        return new FolderListResponse(networkLocationId, result);
    }
    
    @Override
    public List<CategoryCount> selectCategoryCount(Integer networkLocationId) {
        if (networkLocationId == null) {
            return Collections.emptyList();
        }
        return folderItemMapper.selectCategoryCount(networkLocationId);
    }
    
    @Override
    public List<FolderItem> selectByLocationAndCategory(Integer networkLocationId, String fileType) {
        if (networkLocationId == null || fileType == null) {
            return Collections.emptyList();
        }
        return folderItemMapper.selectByLocationAndCategory(networkLocationId, fileType);
    }

    @Override
    public FolderListResponse selectByLocationAndCategoryPaged(Integer networkLocationId, String fileType, String fileName, Integer pageNum, Integer pageSize) {
        if (networkLocationId == null) {
            return new FolderListResponse(null, Collections.emptyList());
        }
        int page = pageNum != null && pageNum > 0 ? pageNum : 1;
        int size = pageSize != null && pageSize > 0 ? pageSize : 30;
        int offset = (page - 1) * size;
        List<FolderItem> list = folderItemMapper.selectByLocationAndCategoryPaged(networkLocationId, fileType, fileName, offset, size);
        return new FolderListResponse(networkLocationId, list);
    }
}
