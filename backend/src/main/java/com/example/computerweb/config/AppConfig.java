package com.example.computerweb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 应用配置类
 * 
 * @author MediaShare Team
 * @version 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    private Smb smb = new Smb();
    private File file = new File();
    private Cache cache = new Cache();
    private List<NetworkShareConfig> networkShares;

    public static class Smb {
        private int timeout = 30000;
        private int retryCount = 3;

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public int getRetryCount() {
            return retryCount;
        }

        public void setRetryCount(int retryCount) {
            this.retryCount = retryCount;
        }
    }

    public static class File {
        private String tempDir = "./temp";
        private String maxPreviewSize = "50MB";
        private String supportedImageFormats = "jpg,jpeg,png,gif,bmp,webp";
        private String supportedVideoFormats = "mp4,avi,mkv,mov,wmv,flv,webm";
        private String supportedAudioFormats = "mp3,wav,flac,aac,ogg";

        public String getTempDir() {
            return tempDir;
        }

        public void setTempDir(String tempDir) {
            this.tempDir = tempDir;
        }

        public String getMaxPreviewSize() {
            return maxPreviewSize;
        }

        public void setMaxPreviewSize(String maxPreviewSize) {
            this.maxPreviewSize = maxPreviewSize;
        }

        public String getSupportedImageFormats() {
            return supportedImageFormats;
        }

        public void setSupportedImageFormats(String supportedImageFormats) {
            this.supportedImageFormats = supportedImageFormats;
        }

        public String getSupportedVideoFormats() {
            return supportedVideoFormats;
        }

        public void setSupportedVideoFormats(String supportedVideoFormats) {
            this.supportedVideoFormats = supportedVideoFormats;
        }

        public String getSupportedAudioFormats() {
            return supportedAudioFormats;
        }

        public void setSupportedAudioFormats(String supportedAudioFormats) {
            this.supportedAudioFormats = supportedAudioFormats;
        }
    }

    public static class Cache {
        private boolean enabled = true;
        private int expireTime = 300;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(int expireTime) {
            this.expireTime = expireTime;
        }
    }

    public static class NetworkShareConfig {
        private String ip;
        private String shareName;
        private String username;
        private String password;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getShareName() {
            return shareName;
        }

        public void setShareName(String shareName) {
            this.shareName = shareName;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public Smb getSmb() {
        return smb;
    }

    public void setSmb(Smb smb) {
        this.smb = smb;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public List<NetworkShareConfig> getNetworkShares() {
        return networkShares;
    }

    public void setNetworkShares(List<NetworkShareConfig> networkShares) {
        this.networkShares = networkShares;
    }
} 