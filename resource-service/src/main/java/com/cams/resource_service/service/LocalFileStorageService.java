package com.cams.resource_service.service;

import com.cams.resource_service.config.LocalStorageConfig;
import com.cams.resource_service.exception.StorageException;
import com.cams.resource_service.model.enums.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalFileStorageService {
    private final LocalStorageConfig storageConfig;

    /**
     * Upload a file to local storage
     */
    @Transactional
    public void storeFile(MultipartFile file, Path filePath, ResourceType type) throws IOException {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        
        // Validate file size
        if (file.getSize() > storageConfig.getMaxSizeBytes()) {
            throw new StorageException("File too large. Maximum size is " + storageConfig.getMaxSizeBytes() + " bytes");
        }
        
        // Validate file type based on resource type
        if (type == ResourceType.DOCUMENT) {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("application/")) {
                throw new StorageException("Invalid file type for document. Must be an application type.");
            }
        } else if (type == ResourceType.VIDEO) {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("video/")) {
                throw new StorageException("Invalid file type for video. Must be a video type.");
            }
        } else if (type == ResourceType.PHOTO) {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new StorageException("Invalid file type for image. Must be an image type.");
            }
        }
        
        // Create directories if they don't exist
        Files.createDirectories(filePath.getParent());
        
        // Store the file
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Get a file from local storage
     */
    @Transactional
    public File getDownloadFile(String filePath) throws Exception {
        if (filePath == null) {
            throw new NullPointerException("filePath is null");
        }
        
        Path path = Paths.get(filePath);
        File file = path.toFile();
        
        // Validate storage path
        if (!path.startsWith(storageConfig.getBaseDir())) {
            throw new SecurityException("Unsupported filename!");
        }
        
        if (!file.exists()) {
            throw new FileNotFoundException("No file found at: " + filePath);
        }
        
        return file;
    }

    /**
     * Delete a file from local storage
     */
    public void deleteFileByPath(Path filePath) throws IOException {
        if (filePath == null) {
            throw new NullPointerException("filePath is null");
        }
        
        // Validate storage path
        if (!filePath.startsWith(Paths.get(LocalStorageConfig.STORAGE_DIRECTORY))) {
            throw new SecurityException("Unsupported filename!");
        }
        
        Files.deleteIfExists(filePath);
    }

    public void deleteFileByString(String filePath) throws IOException {
        if (filePath == null) {
            throw new NullPointerException("filePath is null");
        }
        
        Path path = Paths.get(filePath);
        
        // Validate storage path
        if (!path.startsWith(Paths.get(LocalStorageConfig.STORAGE_DIRECTORY))) {
            throw new SecurityException("Unsupported filename!");
        }
        
        Files.deleteIfExists(path);
    }

    /**
     * Validate file size and type
     */
    private void validateFile(MultipartFile file, ResourceType type) {
        if (file.getSize() > storageConfig.getMaxSizeBytes()) {
            throw new IllegalArgumentException("File size exceeds maximum limit");
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("File type cannot be determined");
        }

        boolean isValid = switch (type) {
            case DOCUMENT -> contentType.matches("application/pdf|application/msword|application/vnd.openxmlformats-officedocument.*|application/vnd.ms-excel|application/vnd.ms-powerpoint");
            case VIDEO -> contentType.startsWith("video/");
            case PHOTO -> contentType.startsWith("image/");
            case FOLDER, LINK -> true;
        };

        if (!isValid) {
            throw new IllegalArgumentException("Invalid file type for resource type: " + type);
        }
    }

    /**
     * Get file size in bytes
     */
    public long getFileSize(String filePath) {
        try {
            return Files.size(Paths.get(filePath));
        } catch (IOException e) {
            log.error("Error getting file size: {}", filePath, e);
            throw new StorageException("Failed to get file size", e);
        }
    }

    /**
     * Get file URL for a resource
     */
    public String getFileUrl(String filePath) {
        // Return relative path from base directory
        Path path = Paths.get(filePath);
        Path baseDir = Paths.get(storageConfig.getBaseDir());
        Path relativePath = baseDir.relativize(path);
        return relativePath.toString();
    }
}
