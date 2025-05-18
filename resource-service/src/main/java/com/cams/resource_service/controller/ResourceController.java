package com.cams.resource_service.controller;

import com.cams.resource_service.dto.CreateResourceRequest;
import com.cams.resource_service.model.ResourceMaterial;
import com.cams.resource_service.model.enums.ResourceType;
import com.cams.resource_service.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResourceMaterial> uploadResource(
            @RequestParam("file") MultipartFile file,
            @Valid @ModelAttribute CreateResourceRequest request) {
        
        ResourceMaterial resource = resourceService.uploadResource(
            file, 
            request.getTitle(),
            request.getDescription(),
            request.getType(),
            request.getCourseSessionId(),
            request.getUploadedBy(),
            request.getCategories()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResourceMaterial> createLinkResource(
            @Valid @RequestBody CreateResourceRequest request) {
        if (request.getType() != ResourceType.LINK) {
            return ResponseEntity.badRequest().build();
        }
        
        ResourceMaterial resource = resourceService.uploadResource(
            null, 
            request.getTitle(),
            request.getDescription(),
            request.getType(),
            request.getCourseSessionId(),
            request.getUploadedBy(),
            request.getCategories()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceMaterial> getResource(@PathVariable Long id) {
        return resourceService.getResourceById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/course-session/{courseSessionId}")
    public ResponseEntity<List<ResourceMaterial>> getResourcesByCourseSession(
            @PathVariable Long courseSessionId) {
        return ResponseEntity.ok(resourceService.getResourcesByCourseSession(courseSessionId));
    }

    @GetMapping("/course-session/{courseSessionId}/type/{type}")
    public ResponseEntity<List<ResourceMaterial>> getResourcesByType(
            @PathVariable Long courseSessionId,
            @PathVariable ResourceType type) {
        return ResponseEntity.ok(resourceService.getResourcesByType(courseSessionId, type));
    }

    @GetMapping("/course-session/{courseSessionId}/category/{category}")
    public ResponseEntity<List<ResourceMaterial>> getResourcesByCategory(
            @PathVariable Long courseSessionId,
            @PathVariable String category) {
        return ResponseEntity.ok(resourceService.getResourcesByCategory(courseSessionId, category));
    }

    @GetMapping("/course-session/{courseSessionId}/search")
    public ResponseEntity<List<ResourceMaterial>> searchResources(
            @PathVariable Long courseSessionId,
            @RequestParam String searchTerm) {
        return ResponseEntity.ok(resourceService.searchResources(courseSessionId, searchTerm));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceMaterial> updateResource(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam List<String> categories) {
        return ResponseEntity.ok(resourceService.updateResource(id, title, description, categories));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/download")
    public ResponseEntity<Void> recordDownload(@PathVariable Long id) {
        resourceService.incrementDownloadCount(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/uploader/{uploadedBy}")
    public ResponseEntity<List<ResourceMaterial>> getResourcesByUploader(
            @PathVariable Long uploadedBy) {
        return ResponseEntity.ok(resourceService.getResourcesByUploader(uploadedBy));
    }
} 