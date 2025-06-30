package com.cams.resource_service.controller;

import com.cams.resource_service.dto.CreateResourceRequest;
import com.cams.resource_service.dto.ResourceResponse;
import com.cams.resource_service.dto.ResourceStatsResponse;
import com.cams.resource_service.dto.UpdateResourceRequest;
import com.cams.resource_service.model.enums.ResourceType;
import com.cams.resource_service.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
@Slf4j
public class ResourceController {

    private final ResourceService resourceService;

    // Upload file resource (for lecturers)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResourceResponse> uploadFileResource(
            @RequestParam("file") MultipartFile file,
            @Valid @ModelAttribute CreateResourceRequest request,
            @RequestHeader("X-User-Id") String uploaderId,
            @RequestHeader("X-User-Role") String role) {
        
        // if (!"LECTURER".equals(role)) {
        //     return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        // }
        
        // For demo purposes, using uploaderId as uploaderName
        String uploaderName = "Lecturer " + uploaderId; // Should be fetched from user service
        
        ResourceResponse resource = resourceService.uploadFile(
            file, request.getTitle(), request.getDescription(), request.getType(),
            request.getCourseSessionId(), Long.parseLong(uploaderId), uploaderName,
            request.getCategories()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    // Create link resource (for lecturers)
    @PostMapping(value = "/link", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResourceResponse> createLinkResource(
            @Valid @RequestBody CreateResourceRequest request,
            @RequestHeader("X-User-Id") String uploaderId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        if (request.getType() != ResourceType.LINK) {
            return ResponseEntity.badRequest().build();
        }
        
        // For demo purposes, using uploaderId as uploaderName
        String uploaderName = "Lecturer " + uploaderId; // Should be fetched from user service
        
        ResourceResponse resource = resourceService.createLink(
            request.getTitle(), request.getDescription(), request.getLinkUrl(),
            request.getCourseSessionId(), Long.parseLong(uploaderId), uploaderName,
            request.getCategories()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    // Get specific resource
    @GetMapping("/{id}")
    public ResponseEntity<ResourceResponse> getResource(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        // Validate access
        if (!resourceService.canAccessResource(id, Long.parseLong(userId), role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        ResourceResponse resource = resourceService.getResourceById(id);
        return ResponseEntity.ok(resource);
    }

    // Get resources by course session
    @GetMapping("/course-session/{courseSessionId}")
    public ResponseEntity<List<ResourceResponse>> getResourcesByCourseSession(
            @PathVariable Long courseSessionId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"STUDENT".equals(role) && !"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<ResourceResponse> resources = resourceService.getResourcesByCourseSession(courseSessionId);
        return ResponseEntity.ok(resources);
    }

    // Get resources stats by course session
    @GetMapping("/course-session/{courseSessionId}/stats")
    public ResponseEntity<ResourceStatsResponse> getResourcesStatsByCourseSession(
            @PathVariable Long courseSessionId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"STUDENT".equals(role) && !"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        ResourceStatsResponse stats = resourceService.getResourcesStatsByCourseSession(courseSessionId);
        return ResponseEntity.ok(stats);
    }

    // Get resources by type
    @GetMapping("/course-session/{courseSessionId}/type/{type}")
    public ResponseEntity<List<ResourceResponse>> getResourcesByType(
            @PathVariable Long courseSessionId,
            @PathVariable ResourceType type,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"STUDENT".equals(role) && !"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<ResourceResponse> resources = resourceService.getResourcesByType(courseSessionId, type);
        return ResponseEntity.ok(resources);
    }

    // Get resources by category
    @GetMapping("/course-session/{courseSessionId}/category/{category}")
    public ResponseEntity<List<ResourceResponse>> getResourcesByCategory(
            @PathVariable Long courseSessionId,
            @PathVariable String category,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"STUDENT".equals(role) && !"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<ResourceResponse> resources = resourceService.getResourcesByCategory(courseSessionId, category);
        return ResponseEntity.ok(resources);
    }

    // Search resources
    @GetMapping("/course-session/{courseSessionId}/search")
    public ResponseEntity<List<ResourceResponse>> searchResources(
            @PathVariable Long courseSessionId,
            @RequestParam String searchTerm,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"STUDENT".equals(role) && !"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<ResourceResponse> resources = resourceService.searchResources(courseSessionId, searchTerm);
        return ResponseEntity.ok(resources);
    }

    // Get resources by uploader (for lecturers to see their own resources)
    @GetMapping("/my-resources")
    public ResponseEntity<List<ResourceResponse>> getMyResources(
            @RequestHeader("X-User-Id") String uploaderId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<ResourceResponse> resources = resourceService.getResourcesByUploader(Long.parseLong(uploaderId));
        return ResponseEntity.ok(resources);
    }

    // Download resource
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadResource(
            // @PathVariable String fileName,
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        try {
            // Validate access
            if (!resourceService.canAccessResource(id, Long.parseLong(userId), role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Get resource details
            ResourceResponse resourceInfo = resourceService.getResourceById(id);
            
            if (resourceInfo.getType() == ResourceType.LINK) {
                return ResponseEntity.badRequest().build();
            }
            
            // Get file resource
            Resource fileResource = resourceService.downloadResource(id);
            
            // Increment download count
            resourceService.incrementDownloadCount(id);
            
            // Determine content type
            String contentType = resourceInfo.getMimeType();
            if (contentType == null) {
                try {
                    contentType = Files.probeContentType(fileResource.getFile().toPath());
                } catch (IOException e) {
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
                }
            }
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + resourceInfo.getOriginalFileName() + "\"")
                .body(fileResource);
                
        } catch (Exception e) {
            log.error("Failed to download resource: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update resource
    @PutMapping("/{id}")
    public ResponseEntity<ResourceResponse> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody UpdateResourceRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Validate ownership
        if (!resourceService.canManageResource(id, Long.parseLong(userId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        ResourceResponse resource = resourceService.updateResource(id, request, Long.parseLong(userId));
        return ResponseEntity.ok(resource);
    }

    // Delete resource
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Validate ownership
        if (!resourceService.canManageResource(id, Long.parseLong(userId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        resourceService.deleteResource(id, Long.parseLong(userId));
        return ResponseEntity.noContent().build();
    }

    // Record download (for analytics)
    @PostMapping("/{id}/download")
    public ResponseEntity<Void> recordDownload(@PathVariable Long id) {
        resourceService.incrementDownloadCount(id);
        return ResponseEntity.ok().build();
    }
}