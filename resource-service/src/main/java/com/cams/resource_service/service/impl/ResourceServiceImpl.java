package com.cams.resource_service.service.impl;

import com.cams.resource_service.client.CourseServiceClient;
import com.cams.resource_service.config.LocalStorageConfig;
import com.cams.resource_service.exception.CourseSessionNotFoundException;
import com.cams.resource_service.exception.ResourceException;
import com.cams.resource_service.exception.StorageException;
import com.cams.resource_service.exception.UnauthorizedAccessException;
import com.cams.resource_service.model.ResourceMaterial;
import com.cams.resource_service.model.enums.ResourceStatus;
import com.cams.resource_service.model.enums.ResourceType;
import com.cams.resource_service.repository.ResourceMaterialRepository;
import com.cams.resource_service.service.LocalFileStorageService;
import com.cams.resource_service.service.ResourceService;
import feign.FeignException;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceMaterialRepository resourceRepository;
    
    @Autowired
    private CourseServiceClient courseServiceClient;
    
    @Autowired
    private LocalFileStorageService fileStorageService;
    
    @Autowired
    private LocalStorageConfig storageConfig;

    @PostConstruct
    private void init() {
        try {
            Files.createDirectories(Paths.get(storageConfig.getBaseDir()));
        } catch (IOException e) {
            log.error("Failed to create base directory", e);
            throw new StorageException("Failed to initialize storage", e);
        }
    }

    @Override
    @Transactional
    public ResourceMaterial uploadResource(MultipartFile file, String title, String description,
                                         ResourceType type, Long courseSessionId, Long uploadedBy,
                                         List<String> categories) {
        try {
            // Validate course session
            courseServiceClient.validateLecturerForCourseSession(uploadedBy, courseSessionId);
            
            // Generate unique filename
            String fileName = file.getOriginalFilename();
            
            // Construct file path using STORAGE_DIRECTORY constant
            String absolutePath = LocalStorageConfig.getFullFilePath(courseSessionId, fileName);
            
            // Store file using absolute path
            fileStorageService.storeFile(file, Paths.get(absolutePath), type);
            
            // Create resource
            ResourceMaterial resource = new ResourceMaterial();
            resource.setTitle(title);
            resource.setDescription(description);
            resource.setType(type);
            resource.setFileUrl(absolutePath);
            resource.setFileName(fileName);
            resource.setOriginalFileName(file.getOriginalFilename());
            resource.setFileSize(file.getSize());
            resource.setUploadedBy(uploadedBy);
            resource.setCourseSessionId(courseSessionId);
            resource.setCategories(new HashSet<>(categories));
            resource.setStatus(ResourceStatus.ACTIVE);
            
            return resourceRepository.save(resource);
        } catch (FeignException.NotFound e) {
            throw new CourseSessionNotFoundException("Course session not found with id: " + courseSessionId);
        } catch (IOException e) {
            throw new StorageException("Failed to store file", e);
        }
    }

    @Override
    public Optional<ResourceMaterial> getResourceById(Long id) {
        return resourceRepository.findById(id);
    }

    @Override
    public List<ResourceMaterial> getResourcesByCourseSession(Long courseSessionId) {
        try {
            // Check if course session exists
            if (!courseServiceClient.checkCourseSessionExists(courseSessionId)) {
                throw new CourseSessionNotFoundException("Course session not found with id: " + courseSessionId);
            }
            return resourceRepository.findByCourseSessionIdAndStatus(courseSessionId, ResourceStatus.ACTIVE);
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new CourseSessionNotFoundException("Course session not found with id: " + courseSessionId);
            }
            throw new RuntimeException("Error validating course session", e);
        }
    }

    @Override
    public List<ResourceMaterial> getResourcesByType(Long courseSessionId, ResourceType type) {
        return resourceRepository.findByCourseSessionIdAndTypeAndStatus(courseSessionId, type, ResourceStatus.ACTIVE);
    }

    @Override
    public List<ResourceMaterial> getResourcesByCategory(Long courseSessionId, String category) {
        return resourceRepository.findByCourseSessionIdAndCategory(courseSessionId, category, ResourceStatus.ACTIVE);
    }

    @Override
    public List<ResourceMaterial> searchResources(Long courseSessionId, String searchTerm) {
        return resourceRepository.searchInCourseSession(courseSessionId, searchTerm, ResourceStatus.ACTIVE);
    }

    @Override
    @Transactional
    public ResourceMaterial updateResource(Long id, String title, String description, List<String> categories) {
        ResourceMaterial resource = resourceRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Resource not found with id: " + id));

        resource.setTitle(title);
        resource.setDescription(description);
        resource.setCategories(new HashSet<>(categories));

        return resourceRepository.save(resource);
    }

    @Override
    @Transactional
    public void deleteResource(Long id) {
        ResourceMaterial resource = resourceRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Resource not found with id: " + id));

        // Validate lecturer permissions before deletion
        validateCourseSession(resource.getCourseSessionId(), resource.getUploadedBy());

        try {
            // Delete the file if it exists (for non-link resources)
            if (resource.getType() != ResourceType.LINK) {
                String filePath = resource.getFileUrl();
                if (filePath != null && !filePath.isEmpty()) {
                    try {
                        fileStorageService.deleteFileByPath(Paths.get(filePath));
                        log.info("Successfully deleted file: {}", filePath);
                    } catch (IOException e) {
                        log.error("Error deleting file: {}", filePath, e);
                        throw new ResourceException("Failed to delete file: " + e.getMessage());
                    // }
                }
            }
            
            // Delete the database record
            resourceRepository.delete(resource);
            log.info("Successfully deleted resource with id: {}", id);
        }
    }catch (Exception e) {
        log.error("Error deleting resource", e);
        throw new ResourceException("Failed to delete resource: " + e.getMessage());}
    }

    @Override
    @Transactional
    public void incrementDownloadCount(Long id) {
        resourceRepository.findById(id).ifPresent(resource -> {
            resource.setDownloadCount(resource.getDownloadCount() + 1);
            resourceRepository.save(resource);
        });
    }

    @Override
    public List<ResourceMaterial> getResourcesByUploader(Long uploadedBy) {
        return resourceRepository.findByUploadedByAndStatus(uploadedBy, ResourceStatus.ACTIVE);
    }

    @Override
    public byte[] getFileContent(ResourceMaterial resource) {
        try {
            if (resource.getType() == ResourceType.LINK) {
                throw new StorageException("Cannot download link resource");
            }
            
            String filePath = resource.getFileUrl();
            return Files.readAllBytes(Paths.get(filePath));
        } catch (IOException e) {
            throw new StorageException("Failed to read file content", e);
        }
    }

    // private void validateFileSize(MultipartFile file) {
    //     if (file.getSize() > storageConfig.getMaxSizeBytes()) {
    //         throw new IllegalArgumentException(
    //             String.format("File size exceeds maximum limit of %d bytes", storageConfig.getMaxSizeBytes())
    //         );
    //     }
    // }

    // private void validateFileType(MultipartFile file, ResourceType type) {
    //     String contentType = file.getContentType();
    //     if (contentType == null) {
    //         throw new IllegalArgumentException("File type cannot be determined");
    //     }

    //     boolean isValid = switch (type) {
    //         case DOCUMENT -> contentType.matches("application/pdf|application/msword|application/vnd.openxmlformats-officedocument.*|application/vnd.ms-excel|application/vnd.ms-powerpoint");
    //         case VIDEO -> contentType.startsWith("video/");
    //         case PHOTO -> contentType.startsWith("image/");
    //         case FOLDER, LINK -> true;
    //     };

    //     if (!isValid) {
    //         throw new IllegalArgumentException("Invalid file type for resource type: " + type);
    //     }
    // }

    private void validateCourseSession(Long courseSessionId, Long lecturerId) {
        try {
            // Check if course session exists
            if (!courseServiceClient.checkCourseSessionExists(courseSessionId)) {
                throw new CourseSessionNotFoundException("Course session not found with id: " + courseSessionId);
            }

            // Validate lecturer's permission for the course session

            if (!courseServiceClient.validateLecturerForCourseSession(lecturerId, courseSessionId)) {
                System.out.println("lecturerId: " + lecturerId);
                System.out.println("courseSessionId: " + courseSessionId);
                System.out.println("#####################################################################################################################");
                throw new UnauthorizedAccessException("Lecturer is not authorized for this course session");
            }
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new CourseSessionNotFoundException("Course session not found with id: " + courseSessionId);
            } else if (e.status() == 403) {
                throw new UnauthorizedAccessException("Lecturer is not authorized for this course session");
            }
            throw new RuntimeException("Error validating course session", e);
        }
    }
}
