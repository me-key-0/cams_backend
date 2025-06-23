package com.cams.resource_service.service.impl;

import com.cams.resource_service.client.CourseServiceClient;
import com.cams.resource_service.config.StorageConfig;
import com.cams.resource_service.dto.ResourceDtoConverter;
import com.cams.resource_service.dto.ResourceResponse;
import com.cams.resource_service.dto.ResourceStatsResponse;
import com.cams.resource_service.dto.UpdateResourceRequest;
import com.cams.resource_service.exception.ResourceNotFoundException;
import com.cams.resource_service.exception.StorageException;
import com.cams.resource_service.exception.UnauthorizedAccessException;
import com.cams.resource_service.model.ResourceMaterial;
import com.cams.resource_service.model.enums.ResourceStatus;
import com.cams.resource_service.model.enums.ResourceType;
import com.cams.resource_service.repository.ResourceMaterialRepository;
import com.cams.resource_service.service.FileStorageService;
import com.cams.resource_service.service.ResourceService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    private final ResourceMaterialRepository resourceRepository;
    private final FileStorageService fileStorageService;
    private final CourseServiceClient courseServiceClient;
    private final StorageConfig storageConfig;

    @Override
    @Transactional
    public ResourceResponse uploadFile(MultipartFile file, String title, String description,
                                     ResourceType type, Long courseSessionId, Long uploadedBy,
                                     String uploaderName, List<String> categories) {
        
        // Validate course session and lecturer authorization
        // validateCourseSessionAccess(courseSessionId, uploadedBy);
        
        try {
            // Store file and get unique filename
            String uniqueFilename = fileStorageService.storeFile(file, courseSessionId, type);
            
            // Create resource entity
            ResourceMaterial resource = new ResourceMaterial();
            resource.setTitle(title);
            resource.setDescription(description);
            resource.setType(type);
            resource.setFileName(uniqueFilename);
            resource.setOriginalFileName(file.getOriginalFilename());
            resource.setFilePath(storageConfig.getFilePath(courseSessionId, uniqueFilename).toString());
            resource.setFileSize(file.getSize());
            resource.setMimeType(file.getContentType());
            resource.setCategories(new HashSet<>(categories));
            resource.setUploadedBy(uploadedBy);
            resource.setUploaderName(uploaderName);
            resource.setCourseSessionId(courseSessionId);
            resource.setStatus(ResourceStatus.ACTIVE);
            resource.setUploadedAt(LocalDateTime.now());
            resource.setDownloadCount(0);
            
            ResourceMaterial savedResource = resourceRepository.save(resource);
            log.info("File resource uploaded successfully: {}", savedResource.getId());
            
            return ResourceDtoConverter.toResponse(savedResource);
            
        } catch (Exception e) {
            log.error("Failed to upload file resource", e);
            throw new StorageException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ResourceResponse createLink(String title, String description, String linkUrl,
                                     Long courseSessionId, Long uploadedBy, String uploaderName,
                                     List<String> categories) {
        
        // Validate course session and lecturer authorization
        validateCourseSessionAccess(courseSessionId, uploadedBy);
        
        try {
            // Create link resource entity
            ResourceMaterial resource = new ResourceMaterial();
            resource.setTitle(title);
            resource.setDescription(description);
            resource.setType(ResourceType.LINK);
            resource.setLinkUrl(linkUrl);
            resource.setFileName("link");
            resource.setOriginalFileName("External Link");
            resource.setFilePath(linkUrl);
            resource.setFileSize(0L);
            resource.setMimeType("text/html");
            resource.setCategories(new HashSet<>(categories));
            resource.setUploadedBy(uploadedBy);
            resource.setUploaderName(uploaderName);
            resource.setCourseSessionId(courseSessionId);
            resource.setStatus(ResourceStatus.ACTIVE);
            resource.setUploadedAt(LocalDateTime.now());
            resource.setDownloadCount(0);
            
            ResourceMaterial savedResource = resourceRepository.save(resource);
            log.info("Link resource created successfully: {}", savedResource.getId());
            
            return ResourceDtoConverter.toResponse(savedResource);
            
        } catch (Exception e) {
            log.error("Failed to create link resource", e);
            throw new StorageException("Failed to create link: " + e.getMessage(), e);
        }
    }

    @Override
    public ResourceResponse getResourceById(Long id) {
        ResourceMaterial resource = resourceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));
        
        return ResourceDtoConverter.toResponse(resource);
    }

    @Override
    public List<ResourceResponse> getResourcesByCourseSession(Long courseSessionId) {
        try {
            // Validate course session exists
            if (!courseServiceClient.checkCourseSessionExists(courseSessionId)) {
                throw new ResourceNotFoundException("Course session not found with id: " + courseSessionId);
            }
            
            List<ResourceMaterial> resources = resourceRepository
                .findByCourseSessionIdAndStatus(courseSessionId, ResourceStatus.ACTIVE);
            
            return ResourceDtoConverter.toResponseList(resources);
            
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new ResourceNotFoundException("Course session not found with id: " + courseSessionId);
            }
            throw new RuntimeException("Error validating course session", e);
        }
    }

    @Override
    public ResourceStatsResponse getResourcesStatsByCourseSession(Long courseSessionId) {
        List<ResourceResponse> resources = getResourcesByCourseSession(courseSessionId);
        
        // Calculate statistics
        Map<String, Integer> resourcesByType = resources.stream()
            .collect(Collectors.groupingBy(
                r -> r.getType().getDisplayName(),
                Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
            ));
        
        Map<String, Integer> resourcesByCategory = resources.stream()
            .flatMap(r -> r.getCategories().stream())
            .collect(Collectors.groupingBy(
                category -> category,
                Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
            ));
        
        long totalFileSize = resources.stream()
            .mapToLong(r -> r.getFileSize() != null ? r.getFileSize() : 0L)
            .sum();
        
        ResourceStatsResponse stats = new ResourceStatsResponse();
        stats.setTotalResources(resources.size());
        stats.setResourcesByType(resourcesByType);
        stats.setResourcesByCategory(resourcesByCategory);
        stats.setResources(resources);
        stats.setTotalFileSize(totalFileSize);
        stats.setTotalFileSizeFormatted(formatFileSize(totalFileSize));
        
        return stats;
    }

    @Override
    public List<ResourceResponse> getResourcesByType(Long courseSessionId, ResourceType type) {
        List<ResourceMaterial> resources = resourceRepository
            .findByCourseSessionIdAndTypeAndStatus(courseSessionId, type, ResourceStatus.ACTIVE);
        
        return ResourceDtoConverter.toResponseList(resources);
    }

    @Override
    public List<ResourceResponse> getResourcesByCategory(Long courseSessionId, String category) {
        List<ResourceMaterial> resources = resourceRepository
            .findByCourseSessionIdAndCategory(courseSessionId, category, ResourceStatus.ACTIVE);
        
        return ResourceDtoConverter.toResponseList(resources);
    }

    @Override
    public List<ResourceResponse> searchResources(Long courseSessionId, String searchTerm) {
        List<ResourceMaterial> resources = resourceRepository
            .searchInCourseSession(courseSessionId, searchTerm, ResourceStatus.ACTIVE);
        
        return ResourceDtoConverter.toResponseList(resources);
    }

    @Override
    public List<ResourceResponse> getResourcesByUploader(Long uploadedBy) {
        List<ResourceMaterial> resources = resourceRepository
            .findByUploadedByAndStatus(uploadedBy, ResourceStatus.ACTIVE);
        
        return ResourceDtoConverter.toResponseList(resources);
    }

    @Override
    public Resource downloadResource(Long resourceId) {
        ResourceMaterial resource = resourceRepository.findById(resourceId)
            .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + resourceId));
        
        if (resource.getType() == ResourceType.LINK) {
            throw new StorageException("Cannot download link resource");
        }
        
        try {
            Path filePath = fileStorageService.getFilePath(resource.getCourseSessionId(), resource.getFileName());
            return new FileSystemResource(filePath);
            
        } catch (Exception e) {
            log.error("Failed to get file for download: {}", resourceId, e);
            throw new StorageException("Failed to prepare file for download", e);
        }
    }

    @Override
    @Transactional
    public void incrementDownloadCount(Long resourceId) {
        resourceRepository.findById(resourceId).ifPresent(resource -> {
            resource.setDownloadCount(resource.getDownloadCount() + 1);
            resourceRepository.save(resource);
            log.debug("Download count incremented for resource: {}", resourceId);
        });
    }

    @Override
    @Transactional
    public ResourceResponse updateResource(Long id, UpdateResourceRequest request, Long updatedBy) {
        ResourceMaterial resource = resourceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));
        
        // Validate ownership
        if (!resource.getUploadedBy().equals(updatedBy)) {
            throw new UnauthorizedAccessException("You can only update your own resources");
        }
        
        // Update fields
        resource.setTitle(request.getTitle());
        resource.setDescription(request.getDescription());
        resource.setCategories(new HashSet<>(request.getCategories()));
        
        // Update link URL for LINK type resources
        if (resource.getType() == ResourceType.LINK && request.getLinkUrl() != null) {
            resource.setLinkUrl(request.getLinkUrl());
            resource.setFilePath(request.getLinkUrl());
        }
        
        ResourceMaterial updatedResource = resourceRepository.save(resource);
        log.info("Resource updated successfully: {}", id);
        
        return ResourceDtoConverter.toResponse(updatedResource);
    }

    @Override
    @Transactional
    public void deleteResource(Long id, Long deletedBy) {
        ResourceMaterial resource = resourceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));
        
        // Validate ownership
        if (!resource.getUploadedBy().equals(deletedBy)) {
            throw new UnauthorizedAccessException("You can only delete your own resources");
        }
        
        try {
            // Delete physical file if it's not a link
            if (resource.getType() != ResourceType.LINK) {
                fileStorageService.deleteFile(resource.getCourseSessionId(), resource.getFileName());
            }
            
            // Delete database record
            resourceRepository.delete(resource);
            log.info("Resource deleted successfully: {}", id);
            
        } catch (Exception e) {
            log.error("Failed to delete resource: {}", id, e);
            throw new StorageException("Failed to delete resource", e);
        }
    }

    @Override
    public boolean canAccessResource(Long resourceId, Long userId, String userRole) {
        try {
            ResourceMaterial resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
            
            // Students and lecturers can access resources in their course sessions
            if ("STUDENT".equals(userRole) || "LECTURER".equals(userRole)) {
                // For students, we would need to check if they're enrolled in the course session
                // For lecturers, we would need to check if they're assigned to the course session
                // For now, allowing access to all active resources
                return resource.getStatus() == ResourceStatus.ACTIVE;
            }
            
            // Admins can access all resources
            return "ADMIN".equals(userRole) || "SUPER_ADMIN".equals(userRole);
            
        } catch (Exception e) {
            log.error("Error checking resource access", e);
            return false;
        }
    }

    @Override
    public boolean canManageResource(Long resourceId, Long userId) {
        try {
            ResourceMaterial resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
            
            // Only the uploader can manage the resource
            return resource.getUploadedBy().equals(userId);
            
        } catch (Exception e) {
            log.error("Error checking resource management permission", e);
            return false;
        }
    }

    private void validateCourseSessionAccess(Long courseSessionId, Long lecturerId) {
        try {
            // Check if course session exists
            if (!courseServiceClient.checkCourseSessionExists(courseSessionId)) {
                throw new ResourceNotFoundException("Course session not found with id: " + courseSessionId);
            }
            
            // Validate lecturer's permission for the course session
            if (!courseServiceClient.validateLecturerForCourseSession(lecturerId, courseSessionId)) {
                throw new UnauthorizedAccessException("Lecturer is not authorized for this course session");
            }
            
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new ResourceNotFoundException("Course session not found with id: " + courseSessionId);
            } else if (e.status() == 403) {
                throw new UnauthorizedAccessException("Lecturer is not authorized for this course session");
            }
            throw new RuntimeException("Error validating course session access", e);
        }
    }

    private String formatFileSize(long bytes) {
        if (bytes == 0) return "0 B";
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.1f %s", size, units[unitIndex]);
    }
}