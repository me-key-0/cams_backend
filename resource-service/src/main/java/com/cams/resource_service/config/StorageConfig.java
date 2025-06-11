package com.cams.resource_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Data
@Component
@ConfigurationProperties(prefix = "app.storage")
public class StorageConfig {
    
    private String baseDirectory = "./resources";
    private long maxFileSize = 50L * 1024 * 1024; // 50MB default
    private String[] allowedMimeTypes = {
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-powerpoint",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "text/plain",
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/bmp",
        "image/svg+xml",
        "image/webp",
        "video/mp4",
        "video/avi",
        "video/quicktime",
        "video/x-msvideo",
        "audio/mpeg",
        "audio/wav",
        "audio/ogg",
        "application/zip",
        "application/x-rar-compressed",
        "application/x-7z-compressed"
    };
    
    public Path getBasePath() {
        return Paths.get(baseDirectory).toAbsolutePath().normalize();
    }
    
    public Path getCourseSessionPath(Long courseSessionId) {
        return getBasePath().resolve(courseSessionId.toString());
    }
    
    public Path getFilePath(Long courseSessionId, String fileName) {
        return getCourseSessionPath(courseSessionId).resolve(fileName);
    }
    
    public boolean isAllowedMimeType(String mimeType) {
        if (mimeType == null) return false;
        
        for (String allowedType : allowedMimeTypes) {
            if (mimeType.equals(allowedType) || mimeType.startsWith(allowedType.split("/")[0] + "/")) {
                return true;
            }
        }
        return false;
    }
}