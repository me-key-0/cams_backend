package com.cams.resource_service.service;

import com.cams.resource_service.dto.ResourceResponse;
import com.cams.resource_service.dto.ResourceStatsResponse;
import com.cams.resource_service.dto.UpdateResourceRequest;
import com.cams.resource_service.model.enums.ResourceType;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceService {
    
    // Upload operations
    ResourceResponse uploadFile(MultipartFile file, String title, String description, 
                               ResourceType type, Long courseSessionId, Long uploadedBy, 
                               String uploaderName, List<String> categories);
    
    ResourceResponse createLink(String title, String description, String linkUrl,
                               Long courseSessionId, Long uploadedBy, String uploaderName,
                               List<String> categories);
    
    // Retrieval operations
    ResourceResponse getResourceById(Long id);
    List<ResourceResponse> getResourcesByCourseSession(Long courseSessionId);
    ResourceStatsResponse getResourcesStatsByCourseSession(Long courseSessionId);
    List<ResourceResponse> getResourcesByType(Long courseSessionId, ResourceType type);
    List<ResourceResponse> getResourcesByCategory(Long courseSessionId, String category);
    List<ResourceResponse> searchResources(Long courseSessionId, String searchTerm);
    List<ResourceResponse> getResourcesByUploader(Long uploadedBy);
    
    // Download operations
    Resource downloadResource(Long resourceId);
    void incrementDownloadCount(Long resourceId);
    
    // Management operations
    ResourceResponse updateResource(Long id, UpdateResourceRequest request, Long updatedBy);
    void deleteResource(Long id, Long deletedBy);
    
    // Validation
    boolean canAccessResource(Long resourceId, Long userId, String userRole);
    boolean canManageResource(Long resourceId, Long userId);
}