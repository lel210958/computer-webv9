package com.example.computerweb.utils;

import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class FileUtils {

    private static final Map<String, String> mineTypeMap = new HashMap<>();

    static {
        mineTypeMap.put(".mp4", "video/mp4");
        mineTypeMap.put(".jpg", "image/jpeg");
        mineTypeMap.put(".jpeg", "image/jpeg");
        mineTypeMap.put(".png", "image/png");
        mineTypeMap.put(".gif", "image/gif");

    }

    // 获取MIME类型
    public static String getMimeType(String filePath) {
        String lower = filePath.toLowerCase();
        for (Map.Entry<String, String> entry : mineTypeMap.entrySet()) {
            String suffix = entry.getKey();
            String mineType = entry.getValue();
            if (lower.endsWith(suffix)) {
                return mineType;
            }
        }
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }
}
