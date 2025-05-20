package com.cams.resource_service.config;

import lombok.Data;
import org.springframework.stereotype.Component;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;

@Data
@Component
public class LocalStorageConfig {
    public static final String STORAGE_DIRECTORY = "/home/me-key-0/Desktop/cams/cams-backend/resources";
    private long maxSizeBytes = 25L * 1024 * 1024; // 25MB default
    
    public Path getBasePath() {
        return Paths.get(STORAGE_DIRECTORY).toAbsolutePath().normalize();
    }

    public String getBaseDir() {
        return STORAGE_DIRECTORY;
    }

    public String getCourseSessionDir(Long courseSessionId) {
        return getBasePath().toString() + "/" + courseSessionId;
    }
    
    public String getFilePath(Long courseSessionId, String fileName) {
        return getBasePath().toString() + "/" + courseSessionId + "/" + fileName;
    }

    public static String getFullFilePath(Long courseSessionId, String fileName) {
        return STORAGE_DIRECTORY + File.separator + courseSessionId + File.separator + fileName;
    }
}
