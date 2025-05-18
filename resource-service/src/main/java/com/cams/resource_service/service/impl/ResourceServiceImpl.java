package com.cams.resource_service.service.impl;

import com.cams.resource_service.client.CourseServiceClient;
import com.cams.resource_service.exception.CourseSessionNotFoundException;
import com.cams.resource_service.exception.UnauthorizedAccessException;
import com.cams.resource_service.model.ResourceMaterial;
import com.cams.resource_service.model.enums.ResourceStatus;
import com.cams.resource_service.model.enums.ResourceType;
import com.cams.resource_service.repository.ResourceMaterialRepository;
import com.cams.resource_service.service.ResourceService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    private final ResourceMaterialRepository resourceRepository;
    private final CourseServiceClient courseServiceClient;
    private final Cloudinary cloudinary;
    
    private static final long MAX_FILE_SIZE = 25L * 1024 * 1024; // 25MB in bytes

    @Override
    @Transactional
    public ResourceMaterial uploadResource(MultipartFile file, String title, String description,
                                         ResourceType type, Long courseSessionId, Long uploadedBy,
                                         List<String> categories) {
        // Validate course session and lecturer permissions
        validateCourseSession(courseSessionId, uploadedBy);
        
        // For link resources, we don't need file validation
        if (type != ResourceType.LINK) {
            validateFileSize(file);
            validateFileType(file, type);
        }

        try {
            String fileUrl;
            String fileName;
            long fileSize = 0;

            // Handle file upload for non-link resources
            if (type != ResourceType.LINK) {
                Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                        "resource_type", getCloudinaryResourceType(type),
                        "folder", "cams/resources/" + courseSessionId
                    )
                );
                fileUrl = uploadResult.get("secure_url").toString();
                fileName = file.getOriginalFilename();
                fileSize = file.getSize();
            } else {
                // For link resources, use the description as the URL
                fileUrl = description;
                fileName = title;
            }

            // Create resource material
            ResourceMaterial resource = ResourceMaterial.builder()
                .title(title)
                .description(description)
                .type(type)
                .fileUrl(fileUrl)
                .fileName(fileName)
                .fileSize(fileSize)
                .courseSessionId(courseSessionId)
                .uploadedBy(uploadedBy)
                .categories(new HashSet<>(categories))
                .status(ResourceStatus.ACTIVE)
                .build();

            return resourceRepository.save(resource);
        } catch (IOException e) {
            log.error("Failed to upload file to Cloudinary", e);
            throw new RuntimeException("Failed to upload file", e);
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

        // Soft delete
        resource.setStatus(ResourceStatus.DELETED);
        resourceRepository.save(resource);

        // TODO: Implement Cloudinary cleanup in a scheduled job
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

    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 25MB");
        }
    }

    private void validateFileType(MultipartFile file, ResourceType type) {
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

    private String getCloudinaryResourceType(ResourceType type) {
        return switch (type) {
            case VIDEO -> "video";
            case PHOTO -> "image";
            default -> "raw";
        };
    }

    private void validateCourseSession(Long courseSessionId, Long lecturerId) {
        try {
            // Check if course session exists
            if (!courseServiceClient.checkCourseSessionExists(courseSessionId)) {
                throw new CourseSessionNotFoundException("Course session not found with id: " + courseSessionId);
            }

            // Validate lecturer's permission for the course session
            if (!courseServiceClient.validateLecturerForCourseSession(lecturerId, courseSessionId)) {
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