package com.example.computerweb.controller;

import com.example.computerweb.config.SmbPools;
import com.example.computerweb.entity.FolderItem;
import com.example.computerweb.entity.NetworkLocationEntity;
import com.example.computerweb.mapper.NetworkLocationMapper;
import com.example.computerweb.model.CategoryCount;
import com.example.computerweb.model.NetworkLocation;
import com.example.computerweb.request.AddNetworkLocationRequest;
import com.example.computerweb.request.CategoryCountRequest;
import com.example.computerweb.request.CategoryFilesRequest;
import com.example.computerweb.request.FolderListRequest;
import com.example.computerweb.request.ScanRequest;
import com.example.computerweb.response.CategoryCountResponse;
import com.example.computerweb.response.FolderListResponse;
import com.example.computerweb.response.NetworkLocationResponse;
import com.example.computerweb.service.FolderItemService;
import com.example.computerweb.service.NetworkLocationService;
import com.example.computerweb.service.FileScanService;
import com.example.computerweb.utils.FileUtils;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/smc/api")
public class NetworkLocationController {

    @Autowired
    private FolderItemService folderItemService;

    @Resource
    private NetworkLocationService networkLocationService;

    @Autowired
    private SmbPools smbPools;

    @Autowired
    private FileScanService fileScanService;
    
    @Resource
    private NetworkLocationMapper networkLocationMapper;

    @GetMapping("/network-locations")
    @ResponseBody
    public NetworkLocationResponse getNetworkLocations() {
        return networkLocationService.getNetworkLocations();
    }

    @PostMapping("/network-location/add")
    @ResponseBody
    public Map<String, Object> addNetworkLocation(@RequestBody AddNetworkLocationRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 验证必填字段
            if (request.getIp() == null || request.getIp().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "IP地址不能为空");
                return result;
            }
            if (request.getPath() == null || request.getPath().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "共享名称不能为空");
                return result;
            }
            if (request.getUserName() == null || request.getUserName().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "用户名不能为空");
                return result;
            }
            
            // 创建网络位置实体
            NetworkLocationEntity entity = new NetworkLocationEntity();
            entity.setIp(request.getIp().trim());
            entity.setPath(request.getPath().trim());
            entity.setUserName(request.getUserName().trim());
            entity.setPwd(request.getPwd());
            
            // 插入数据库
            int insertResult = networkLocationMapper.insert(entity);
            
            if (insertResult > 0) {
                result.put("success", true);
                result.put("message", "网络位置添加成功");
                result.put("id", entity.getId());
            } else {
                result.put("success", false);
                result.put("message", "网络位置添加失败");
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加网络位置时发生错误: " + e.getMessage());
        }
        
        return result;
    }

    @PostMapping("/network-location/category-count")
    @ResponseBody
    public CategoryCountResponse getCategoryCount(@RequestBody CategoryCountRequest request) {
        // 从数据库查询分类统计
        List<CategoryCount> categoryCounts = folderItemService.selectCategoryCount(request.getNetworkLocationId());
        
        return new CategoryCountResponse(request.getNetworkLocationId(), categoryCounts);
    }

    @PostMapping("/network-location/category-files")
    @ResponseBody
    public FolderListResponse getCategoryFiles(@RequestBody CategoryFilesRequest request) {
        String fileType = convertCategoryToFileType(request.getType());
        return folderItemService.selectByLocationAndCategoryPaged(
            request.getNetworkLocationId(),
            fileType,
            request.getFileName(),
            request.getPageNum(),
            request.getPageSize()
        );
    }
    
    /**
     * 将前端分类类型转换为数据库文件类型
     */
    private String convertCategoryToFileType(String categoryType) {
        switch (categoryType) {
            case "video": return "VIDEO";
            case "image": return "IMAGE";
            case "doc": return "DOCUMENT";
            case "zip": return "ZIP";
            case "music": return "MUSIC";
            default: return "OTHER";
        }
    }

    @PostMapping("/network-location/folder-list")
    @ResponseBody
    public FolderListResponse getFolderList(@RequestBody FolderListRequest request) {
        return folderItemService.selectByLocationAndParentPath(request);
    }

    // 直接流式播放接口，前端传递networkLocationId和filePath参数
    @GetMapping("/network-location/stream")
    public void streamFile(@RequestParam Integer networkLocationId,
                          @RequestParam String filePath,
                          HttpServletRequest request,
                          HttpServletResponse response) throws Exception {
        // 从数据库获取网络位置信息
        NetworkLocationEntity networkLocation = networkLocationMapper.selectById(networkLocationId);
        if (networkLocation == null) {
            throw new RuntimeException("未找到对应的网络位置配置");
        }
        
        String ip = networkLocation.getIp();
        String shareName = networkLocation.getPath();
        String smbUser = networkLocation.getUserName();
        String smbPwd = networkLocation.getPwd();
        
        // 标准化filePath，全部用反斜杠，去除多余分隔符
        String smbFilePath = filePath.replace("/", "\\").replaceAll("\\\\+", "\\\\");
        while (smbFilePath.startsWith("\\")) smbFilePath = smbFilePath.substring(1);
        while (smbFilePath.endsWith("\\")) smbFilePath = smbFilePath.substring(0, smbFilePath.length() - 1);
        GenericObjectPool<Session> pool = smbPools.getOrCreatePool(ip, smbUser, smbPwd);
        Session session = pool.borrowObject();
        try {
            DiskShare share = (DiskShare) session.connectShare(shareName);
            File smbFile = share.openFile(
                smbFilePath,
                java.util.EnumSet.of(AccessMask.GENERIC_READ),
                null,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                null
            );
            long fileLength = smbFile.getFileInformation().getStandardInformation().getEndOfFile();
            String range = request.getHeader("Range");
            long start = 0, end = fileLength - 1;
            if (range != null && range.startsWith("bytes=")) {
                String[] arr = range.substring(6).split("-");
                start = Long.parseLong(arr[0]);
                if (arr.length > 1 && !arr[1].isEmpty()) end = Long.parseLong(arr[1]);
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            }
            long contentLength = end - start + 1;
            response.setHeader("Content-Type", FileUtils.getMimeType(filePath));
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Content-Length", String.valueOf(contentLength));
            if (range != null) {
                response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
            }
            try (InputStream in = smbFile.getInputStream()) {
                in.skip(start);
                byte[] buf = new byte[8192];
                long toRead = contentLength;
                OutputStream out = response.getOutputStream();
                int len;
                while (toRead > 0 && (len = in.read(buf, 0, (int)Math.min(buf.length, toRead))) != -1) {
                    out.write(buf, 0, len);
                    toRead -= len;
                }
            }
        } finally {
            pool.returnObject(session);
        }
    }

    @PostMapping("/network-location/scan")
    @ResponseBody
    public Map<String, Object> scanNetworkLocation(@RequestBody ScanRequest request) {
        Integer networkLocationId = request.getNetworkLocationId();
        
        if (networkLocationId == null) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("scanStatus", "ERROR");
            errorResult.put("message", "网络位置ID不能为空");
            return errorResult;
        }
        
        // 从数据库获取网络位置信息
        NetworkLocationEntity networkLocationEntity = networkLocationMapper.selectById(networkLocationId);
        if (networkLocationEntity == null) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("scanStatus", "ERROR");
            errorResult.put("message", "未找到对应的网络位置配置");
            return errorResult;
        }
        
        // 构建NetworkLocation对象
        NetworkLocation networkLocation = new NetworkLocation();
        networkLocation.setIp(networkLocationEntity.getIp());
        networkLocation.setName(networkLocationEntity.getPath());
        networkLocation.setUser(networkLocationEntity.getUserName());
        networkLocation.setPwd(networkLocationEntity.getPwd() != null ? networkLocationEntity.getPwd() : "");
        
        // 调用扫描服务
        return fileScanService.scanNetworkLocation(networkLocation);
    }
}