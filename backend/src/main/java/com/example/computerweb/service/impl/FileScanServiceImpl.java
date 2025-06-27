package com.example.computerweb.service.impl;

import com.example.computerweb.config.SmbPools;
import com.example.computerweb.entity.FolderItem;
import com.example.computerweb.mapper.FolderItemMapper;
import com.example.computerweb.mapper.NetworkLocationMapper;
import com.example.computerweb.entity.NetworkLocationEntity;
import com.example.computerweb.model.NetworkLocation;
import com.example.computerweb.service.FileScanService;
import com.example.computerweb.utils.FileUtils;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.msfscc.fileinformation.FileStandardInformation;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class FileScanServiceImpl implements FileScanService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileScanServiceImpl.class);
    
    @Resource
    private FolderItemMapper folderItemMapper;
    
    @Resource
    private NetworkLocationMapper networkLocationMapper;
    
    @Autowired
    private SmbPools smbPools;
    
    @Override
    public Map<String, Object> scanNetworkLocation(NetworkLocation networkLocation) {
        Map<String, Object> result = new HashMap<>();

        try {
            logger.info("开始扫描网络位置: {}:{}", networkLocation.getIp(), networkLocation.getName());
            logger.info("连接信息 - IP: {}, 共享名: {}, 用户: {}, 密码长度: {}", 
                networkLocation.getIp(), 
                networkLocation.getName(), 
                networkLocation.getUser(),
                networkLocation.getPwd() != null ? networkLocation.getPwd().length() : 0);
            
            // 验证输入参数
            if (networkLocation.getIp() == null || networkLocation.getIp().trim().isEmpty()) {
                throw new IllegalArgumentException("IP地址不能为空");
            }
            if (networkLocation.getName() == null || networkLocation.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("共享名不能为空");
            }
            if (networkLocation.getUser() == null || networkLocation.getUser().trim().isEmpty()) {
                throw new IllegalArgumentException("用户名不能为空");
            }
            
            // 查找网络位置ID
            Integer networkLocationId = findNetworkLocationId(networkLocation);
            if (networkLocationId == null) {
                throw new IllegalArgumentException("未找到对应的网络位置配置");
            }
            
            // 获取SMB连接池
            logger.debug("正在获取SMB连接池...");
            GenericObjectPool<Session> pool = smbPools.getOrCreatePool(
                networkLocation.getIp(), 
                networkLocation.getUser(), 
                networkLocation.getPwd()
            );
            
            logger.debug("正在从连接池获取会话...");
            Session session = pool.borrowObject();
            try {
                logger.debug("正在连接到共享: {}", networkLocation.getName());
                DiskShare share = (DiskShare) session.connectShare(networkLocation.getName());
                logger.info("成功连接到共享: {}", networkLocation.getName());
                
                // 扫描文件系统
                logger.debug("开始扫描文件系统...");
                List<FolderItem> scannedFiles = scanDirectory(share, "", networkLocationId);
                logger.info("文件系统扫描完成，共发现{}个文件/文件夹", scannedFiles.size());
                
                // 获取数据库中的现有记录
                logger.debug("正在查询数据库中的现有记录...");
                List<FolderItem> existingFiles = folderItemMapper.selectByNetworkLocation(networkLocationId);
                logger.info("数据库查询完成，现有记录{}个", existingFiles.size());
                
                // 同步数据库
                logger.debug("开始同步数据库...");
                Map<String, Object> syncResult = syncDatabase(scannedFiles, existingFiles, networkLocationId);
                
                result.put("networkLocation", networkLocation);
                result.put("scanStatus", "SUCCESS");
                result.put("scannedFiles", scannedFiles.size());
                result.put("existingFiles", existingFiles.size());
                result.put("newFiles", syncResult.get("newFiles"));
                result.put("updatedFiles", syncResult.get("updatedFiles"));
                result.put("deletedFiles", syncResult.get("deletedFiles"));
                result.put("message", "扫描完成");
                
                logger.info("扫描完成: 扫描{}个文件, 新增{}个, 更新{}个, 删除{}个", 
                    scannedFiles.size(), 
                    syncResult.get("newFiles"), 
                    syncResult.get("updatedFiles"), 
                    syncResult.get("deletedFiles"));
                
            } finally {
                logger.debug("正在归还会话到连接池...");
                pool.returnObject(session);
            }
            
        } catch (Exception e) {
            logger.error("扫描网络位置失败: {}:{}", networkLocation.getIp(), networkLocation.getName(), e);
            result.put("networkLocation", networkLocation);
            result.put("scanStatus", "ERROR");
            result.put("message", "扫描失败: " + e.getMessage());
            result.put("errorDetails", e.getClass().getSimpleName() + ": " + e.getMessage());
            
            // 提供更详细的错误信息
            if (e instanceof java.net.ConnectException) {
                result.put("errorType", "CONNECTION_ERROR");
                result.put("suggestion", "请检查网络连接和SMB服务是否启动");
            } else if (e instanceof java.lang.NullPointerException) {
                result.put("errorType", "NULL_POINTER_ERROR");
                result.put("suggestion", "请检查认证信息是否正确");
            } else if (e.getMessage() != null && e.getMessage().contains("authentication")) {
                result.put("errorType", "AUTHENTICATION_ERROR");
                result.put("suggestion", "请检查用户名和密码是否正确");
            } else {
                result.put("errorType", "UNKNOWN_ERROR");
                result.put("suggestion", "请查看详细错误信息并联系管理员");
            }
        }
        
        return result;
    }
    
    /**
     * 根据网络位置信息查找对应的ID
     */
    private Integer findNetworkLocationId(NetworkLocation networkLocation) {
        List<NetworkLocationEntity> allLocations = networkLocationMapper.selectAll();
        
        for (NetworkLocationEntity entity : allLocations) {
            if (entity.getIp().equals(networkLocation.getIp()) && 
                entity.getPath().equals(networkLocation.getName())) {
                return entity.getId();
            }
        }
        
        return null;
    }
    
    /**
     * 递归扫描目录
     */
    private List<FolderItem> scanDirectory(DiskShare share, String currentPath, Integer networkLocationId) {
        List<FolderItem> files = new ArrayList<>();
        
        try {
            // 列出当前目录下的所有文件和文件夹
            for (FileIdBothDirectoryInformation fileInfo : share.list(currentPath)) {
                String fileName = fileInfo.getFileName();
                
                // 跳过系统文件
                if (fileName.equals(".") || fileName.equals("..") || fileName.startsWith("$")) {
                    continue;
                }
                
                String filePath = currentPath.isEmpty() ? fileName : currentPath + "\\" + fileName;
                String parentPath = currentPath;
                
                FolderItem folderItem = new FolderItem();
                folderItem.setFileName(fileName);
                folderItem.setFilePath(filePath);
                folderItem.setParentPath(parentPath);
                folderItem.setNetworkLocationId(networkLocationId);
                
                // 获取文件信息 - 使用基本属性
                folderItem.setFileSize(fileInfo.getEndOfFile());
                folderItem.setLastModify(new Date(fileInfo.getLastWriteTime().toEpochMillis()));
                
                // 判断是否为目录
                boolean isDirectory = (fileInfo.getFileAttributes() & 0x10) != 0; // FILE_ATTRIBUTE_DIRECTORY
                folderItem.setIsDirectory(isDirectory ? 1 : 0);
                
                // 设置文件类型
                if (isDirectory) {
                    folderItem.setFileType("DIRECTORY");
                } else {
                    folderItem.setFileType(getFileType(fileName));
                }
                
                files.add(folderItem);
                
                // 如果是目录，递归扫描
                if (isDirectory) {
                    files.addAll(scanDirectory(share, filePath, networkLocationId));
                }
            }
            
        } catch (Exception e) {
            logger.warn("扫描目录失败: {}", currentPath, e);
        }
        
        return files;
    }
    
    /**
     * 根据文件扩展名获取文件类型
     */
    private String getFileType(String fileName) {
        String lowerFileName = fileName.toLowerCase();
        if (lowerFileName.endsWith(".mp4") || lowerFileName.endsWith(".avi") || lowerFileName.endsWith(".mkv") || 
            lowerFileName.endsWith(".mov") || lowerFileName.endsWith(".wmv") || lowerFileName.endsWith(".flv")) {
            return "VIDEO";
        } else if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg") || lowerFileName.endsWith(".png") || 
                   lowerFileName.endsWith(".gif") || lowerFileName.endsWith(".bmp") || lowerFileName.endsWith(".webp")) {
            return "IMAGE";
        } else if (lowerFileName.endsWith(".mp3") || lowerFileName.endsWith(".wav") || lowerFileName.endsWith(".flac") || 
                   lowerFileName.endsWith(".aac") || lowerFileName.endsWith(".ogg")) {
            return "MUSIC";
        } else if (lowerFileName.endsWith(".pdf") || lowerFileName.endsWith(".doc") || lowerFileName.endsWith(".docx") || 
                   lowerFileName.endsWith(".txt") || lowerFileName.endsWith(".xls") || lowerFileName.endsWith(".xlsx")) {
            return "DOCUMENT";
        } else if (lowerFileName.endsWith(".zip") || lowerFileName.endsWith(".rar") || lowerFileName.endsWith(".7z") || 
                   lowerFileName.endsWith(".tar") || lowerFileName.endsWith(".gz")) {
            return "ZIP";
        } else {
            return "OTHER";
        }
    }
    
    /**
     * 同步数据库
     */
    private Map<String, Object> syncDatabase(List<FolderItem> scannedFiles, List<FolderItem> existingFiles, Integer networkLocationId) {
        Map<String, Object> result = new HashMap<>();
        int newFiles = 0, updatedFiles = 0, deletedFiles = 0;
        
        // 创建现有文件的映射，用于快速查找
        Map<String, FolderItem> existingFileMap = new HashMap<>();
        for (FolderItem file : existingFiles) {
            existingFileMap.put(file.getFilePath(), file);
        }
        
        // 创建扫描文件的映射，用于快速查找
        Map<String, FolderItem> scannedFileMap = new HashMap<>();
        for (FolderItem file : scannedFiles) {
            scannedFileMap.put(file.getFilePath(), file);
        }
        
        // 处理扫描到的文件
        for (FolderItem scannedFile : scannedFiles) {
            FolderItem existingFile = existingFileMap.get(scannedFile.getFilePath());
            
            if (existingFile == null) {
                // 新文件，插入数据库
                folderItemMapper.insertFolderItem(scannedFile);
                newFiles++;
                logger.debug("新增文件: {}", scannedFile.getFilePath());
            } else {
                // 文件已存在，检查是否需要更新
                if (needsUpdate(scannedFile, existingFile)) {
                    scannedFile.setId(existingFile.getId());
                    folderItemMapper.updateFolderItem(scannedFile);
                    updatedFiles++;
                    logger.debug("更新文件: {}", scannedFile.getFilePath());
                }
            }
        }
        
        // 删除数据库中已不存在的文件
        for (FolderItem existingFile : existingFiles) {
            if (!scannedFileMap.containsKey(existingFile.getFilePath())) {
                folderItemMapper.deleteByNetworkLocation(networkLocationId);
                deletedFiles++;
                logger.debug("删除文件: {}", existingFile.getFilePath());
            }
        }
        
        result.put("newFiles", newFiles);
        result.put("updatedFiles", updatedFiles);
        result.put("deletedFiles", deletedFiles);
        
        return result;
    }
    
    /**
     * 判断文件是否需要更新
     */
    private boolean needsUpdate(FolderItem scannedFile, FolderItem existingFile) {
        return !Objects.equals(scannedFile.getFileSize(), existingFile.getFileSize()) ||
               !Objects.equals(scannedFile.getLastModify(), existingFile.getLastModify()) ||
               !Objects.equals(scannedFile.getIsDirectory(), existingFile.getIsDirectory());
    }
} 