package com.cams.resource_service.service;

import com.cams.resource_service.model.ResourceMaterial;
import com.cams.resource_service.model.enums.ResourceStatus;
import com.cams.resource_service.model.enums.ResourceType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing resource materials.
 */
public interface ResourceService {
    
    /**
     * Upload a new resource material
     */
    ResourceMaterial uploadResource(MultipartFile file, String title, String description, 
                                  ResourceType type, Long courseSessionId, Long uploadedBy,
                                  List<String> categories);

    /**
     * Get a resource by its ID
     */
    Optional<ResourceMaterial> getResourceById(Long id);

    /**
     * Get all resources for a course session
     */
    List<ResourceMaterial> getResourcesByCourseSession(Long courseSessionId);

    /**
     * Get resources by type for a course session
     */
    List<ResourceMaterial> getResourcesByType(Long courseSessionId, ResourceType type);

    /**
     * Get resources by category for a course session
     */
    List<ResourceMaterial> getResourcesByCategory(Long courseSessionId, String category);

    /**
     * Search resources by title or description
     */
    List<ResourceMaterial> searchResources(Long courseSessionId, String searchTerm);

    /**
     * Update resource details
     */
    ResourceMaterial updateResource(Long id, String title, String description, 
                                  List<String> categories);

    /**
     * Delete a resource (soft delete)
     */
    void deleteResource(Long id);

    /**
     * Increment download count for a resource
     */
    void incrementDownloadCount(Long id);

    /**
     * Get all resources uploaded by a specific user
     */
    List<ResourceMaterial> getResourcesByUploader(Long uploadedBy);
} 