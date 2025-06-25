package com.example.computerweb.service.impl;

import com.example.computerweb.entity.FolderItem;
import com.example.computerweb.mapper.FolderItemMapper;
import com.example.computerweb.request.FolderListRequest;
import com.example.computerweb.response.FolderListResponse;
import com.example.computerweb.service.FolderItemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FolderItemServiceImpl implements FolderItemService {

    @Resource
    private FolderItemMapper folderItemMapper;

    @Override
    public FolderListResponse selectByLocationAndParentPath(FolderListRequest request) {
        String currentPath = request.getCurrentPath();
        String networkShareLocation = String.format("\\\\%s\\%s", request.getNetworkLocation().getIp(), request.getNetworkLocation().getName());
        // 查询数据库
        List<FolderItem> folderList = folderItemMapper.selectByLocationAndParentPath(networkShareLocation, currentPath == null ? "" : currentPath);
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
        return new FolderListResponse(request.getNetworkLocation(), result);
    }
}
