package com.example.computerweb.controller;

import com.example.computerweb.config.SmbPools;
import com.example.computerweb.entity.FolderItem;
import com.example.computerweb.model.CategoryCount;
import com.example.computerweb.request.CategoryCountRequest;
import com.example.computerweb.request.CategoryFilesRequest;
import com.example.computerweb.request.FolderListRequest;
import com.example.computerweb.response.CategoryCountResponse;
import com.example.computerweb.response.FolderListResponse;
import com.example.computerweb.response.NetworkLocationResponse;
import com.example.computerweb.service.FolderItemService;
import com.example.computerweb.service.NetworkLocationService;
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
import java.util.List;

@Controller
@RequestMapping("/smc/api")
public class NetworkLocationController {

    @Autowired
    private FolderItemService folderItemService;

    @Resource
    private NetworkLocationService networkLocationService;

    @Autowired
    private SmbPools smbPools;

    @GetMapping("/network-locations")
    @ResponseBody
    public NetworkLocationResponse getNetworkLocations() {
        return networkLocationService.getNetworkLocations();
    }

    @PostMapping("/network-location/category-count")
    @ResponseBody
    public CategoryCountResponse getCategoryCount(@RequestBody CategoryCountRequest request) {
        // 模拟数据，实际项目中应该根据网络位置查询数据库
        List<CategoryCount> categoryCounts = Arrays.asList(
            new CategoryCount("video", "102"),
            new CategoryCount("image", "103"),
            new CategoryCount("doc", "104"),
            new CategoryCount("zip", "105"),
            new CategoryCount("music", "106")
        );
        
        return new CategoryCountResponse(request.getNetworkLocation(), categoryCounts);
    }

    @PostMapping("/network-location/category-files")
    @ResponseBody
    public FolderListResponse getCategoryFiles(@RequestBody CategoryFilesRequest request) {
        // 模拟数据，根据分类类型返回对应的文件列表
        List<FolderItem> folderList = new ArrayList<>();
        
        switch (request.getType()) {
            case "video":
                folderList = Arrays.asList(
                    new FolderItem("20250531003708_0.mp4", "0", "1463226", "VIDEO", "抖音\\不知名岛\\20250531003708_0.mp4", "1719089215000"),
                    new FolderItem("不知名岛_7487255697881599284__穿搭分享_反差闪一下.mp4", "0", "2770083", "VIDEO", "抖音\\不知名岛\\不知名岛_7487255697881599284__穿搭分享_反差闪一下.mp4", "1719089215000"),
                    new FolderItem("IPX-534.mp4", "0", "6352127652", "VIDEO", "IPX-534.mp4", "1719089215000")
                );
                break;
            case "image":
                folderList = Arrays.asList(
                    new FolderItem("photo1.jpg", "0", "1024000", "IMAGE", "images\\photo1.jpg", "1719089215000"),
                    new FolderItem("photo2.png", "0", "2048000", "IMAGE", "images\\photo2.png", "1719089215000")
                );
                break;
            case "doc":
                folderList = Arrays.asList(
                    new FolderItem("document1.pdf", "0", "512000", "DOCUMENT", "docs\\document1.pdf", "1719089215000"),
                    new FolderItem("document2.docx", "0", "256000", "DOCUMENT", "docs\\document2.docx", "1719089215000")
                );
                break;
            case "music":
                folderList = Arrays.asList(
                    new FolderItem("song1.mp3", "0", "8192000", "MUSIC", "music\\song1.mp3", "1719089215000"),
                    new FolderItem("song2.wav", "0", "16384000", "MUSIC", "music\\song2.wav", "1719089215000")
                );
                break;
            case "zip":
                folderList = Arrays.asList(
                    new FolderItem("archive1.zip", "0", "10485760", "ZIP", "archives\\archive1.zip", "1719089215000"),
                    new FolderItem("archive2.rar", "0", "20971520", "ZIP", "archives\\archive2.rar", "1719089215000")
                );
                break;
            default:
                folderList = new ArrayList<>();
        }
        
        return new FolderListResponse(request.getNetworkLocation(), folderList);
    }

    @PostMapping("/network-location/folder-list")
    @ResponseBody
    public FolderListResponse getFolderList(@RequestBody FolderListRequest request) {
        return folderItemService.selectByLocationAndParentPath(request);
    }

    // 直接流式播放接口，前端传递networkLocation和filePath参数
    @GetMapping("/network-location/stream")
    public void streamFile(@RequestParam String ip,
                          @RequestParam String shareName,
                          @RequestParam String filePath,
                          HttpServletRequest request,
                          HttpServletResponse response) throws Exception {
        // TODO: 这里请替换为实际SMB用户名密码
        String smbUser = "lel0958_share";
        String smbPwd = "lel@210958_SH";
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
}