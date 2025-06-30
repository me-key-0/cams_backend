package com.cams.resource_service.service;

import com.cams.resource_service.config.StorageConfig;
import com.cams.resource_service.exception.StorageException;
import com.cams.resource_service.model.enums.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {
    
    private final StorageConfig storageConfig;

    public String storeFile(MultipartFile file, Long courseSessionId, ResourceType type) {
        validateFile(file, type);
        
        try {
            // Create directory if it doesn't exist
            Path courseSessionDir = storageConfig.getCourseSessionPath(courseSessionId);
            Files.createDirectories(courseSessionDir);
            
            // Generate unique filename
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID().toString() + 
                (fileExtension.isEmpty() ? "" : "." + fileExtension);
            
            // Store file
            Path targetLocation = courseSessionDir.resolve(uniqueFilename);
            
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }
            
            log.info("File stored successfully: {}", targetLocation);
            return uniqueFilename;
            
        } catch (IOException e) {
            log.error("Failed to store file", e);
            throw new StorageException("Failed to store file: " + e.getMessage(), e);
        }
    }

    public Path getFilePath(Long courseSessionId, String fileName) {
        Path filePath = storageConfig.getFilePath(courseSessionId, fileName);
        
        if (!Files.exists(filePath)) {
            log.error("File not found: {}", filePath);
            throw new StorageException("File not found: " + fileName);
        }
        
        return filePath;
    }

    public void deleteFile(Long courseSessionId, String fileName) {
        try {
            Path filePath = storageConfig.getFilePath(courseSessionId, fileName);
            Files.deleteIfExists(filePath);
            log.info("File deleted successfully: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", fileName, e);
            throw new StorageException("Failed to delete file: " + e.getMessage(), e);
        }
    }

    public boolean fileExists(Long courseSessionId, String fileName) {
        Path filePath = storageConfig.getFilePath(courseSessionId, fileName);
        return Files.exists(filePath);
    }

    public long getFileSize(Long courseSessionId, String fileName) {
        try {
            Path filePath = storageConfig.getFilePath(courseSessionId, fileName);
            return Files.size(filePath);
        } catch (IOException e) {
            log.error("Failed to get file size: {}", fileName, e);
            return 0;
        }
    }

    private void validateFile(MultipartFile file, ResourceType type) {
        if (file.isEmpty()) {
            throw new StorageException("Cannot store empty file");
        }

        if (file.getSize() > storageConfig.getMaxFileSize()) {
            throw new StorageException("File size exceeds maximum limit of " + 
                formatFileSize(storageConfig.getMaxFileSize()));
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..")) {
            throw new StorageException("Invalid filename: " + originalFilename);
        }

        // Validate file extension
        String extension = getFileExtension(originalFilename);
        if (!type.isValidExtension(extension)) {
            throw new StorageException("Invalid file extension '" + extension + 
                "' for resource type " + type.getDisplayName());
        }

        // Validate MIME type
        String mimeType = file.getContentType();
        if (!storageConfig.isAllowedMimeType(mimeType)) {
            throw new StorageException("File type not allowed: " + mimeType);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private String formatFileSize(long bytes) {
        String[] units = {"B", "KB", "MB", "GB"};
        int unitIndex = 0;
        double size = bytes;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.1f %s", size, units[unitIndex]);
    }
}