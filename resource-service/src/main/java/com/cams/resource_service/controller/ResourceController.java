package com.cams.resource_service.controller;

import com.cams.resource_service.dto.CreateResourceRequest;
import com.cams.resource_service.model.ResourceMaterial;
import com.cams.resource_service.model.enums.ResourceType;
import com.cams.resource_service.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import com.cams.resource_service.exception.ResourceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import java.io.File;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import com.cams.resource_service.config.LocalStorageConfig;

@RestController
@RequestMapping("/api/v1/resources")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;
    
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

//     @GetMapping("/download/{fileName}")
//     public ResponseEntity<Resource> downloadResource(@PathVariable String fileName, @RequestParam Long id) {
//         try {
//             // Get the resource by filename
//             ResourceMaterial resource = resourceService.getResourceById(id)
//                     .orElseThrow(() -> new EmptyResultDataAccessException(1));

//             if (resource.getType() == ResourceType.LINK) {
//                 FileSystemResource fileSystemResource = new FileSystemResource(resource.getFileUrl());
//                 return ResponseEntity.ok()
//                     .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFileName())
//                     // .contentLength(resource.getFileUrl().length())
//                     .contentLength(fileSystemResource.contentLength())
//                     .contentType(MediaType.TEXT_PLAIN)
//                     .body(fileSystemResource);
//             }

//             // Construct the file path using course session ID
//             File file = new File(LocalStorageConfig.getFullFilePath(resource.getCourseSessionId(), fileName));
//             if (!file.exists() || !file.canRead()) {
//     throw new ResourceException("File does not exist or cannot be read.");
// }

            
//             // Use InputStreamResource to stream the file content
//             // InputStreamResource inputStreamResource = new InputStreamResource(Files.newInputStream(file.toPath()));
//             Resource fileResource = new FileSystemResource(file);

            
//             return ResponseEntity.ok()
//                     .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getOriginalFileName())
//                     .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                     .contentLength(file.length())
//                     .body(fileResource);
//         } catch (IOException e) {
//             throw new ResourceException("Failed to read file: " + e.getMessage());
//         } catch (Exception e) {
//             throw new ResourceException("Failed to download file: " + e.getMessage());
//         }
//     }

    // private MediaType getMediaTypeForFile(File file) {
    //     try {
    //         String contentType = Files.probeContentType(file.toPath());
    //         return contentType != null ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM;
    //     } catch (IOException e) {
    //         return MediaType.APPLICATION_OCTET_STREAM;
    //     }
    // }

    @GetMapping("/download/{fileName}/{id}")
public ResponseEntity<Resource> downloadResource(@PathVariable String fileName, @PathVariable Long id) {
    try {
        System.out.println("Received download request for file: " + fileName + ", with id: " + id);

        ResourceMaterial resource = resourceService.getResourceById(id)
                .orElseThrow(() -> new EmptyResultDataAccessException(1));

        if (resource.getType() == ResourceType.LINK) {
            File file = new File(resource.getFileUrl());

            System.out.println("Serving LINK file: " + file.getAbsolutePath());
            System.out.println("File exists: " + file.exists());
            System.out.println("File readable: " + file.canRead());
            System.out.println("File length: " + file.length());

            FileSystemResource fileResource = new FileSystemResource(file);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFileName() + "\"")
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileResource);
        }

        String fullPath = LocalStorageConfig.getFullFilePath(resource.getCourseSessionId(), fileName);
        File file = new File(fullPath);

        System.out.println("Constructed file path: " + fullPath);
        System.out.println("File exists: " + file.exists());
        System.out.println("File readable: " + file.canRead());
        System.out.println("File length: " + file.length());

        if (!file.exists() || !file.canRead()) {
            throw new ResourceException("File not found or not readable.");
        }

        FileSystemResource fileResource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getOriginalFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(fileResource);

    } catch (Exception e) {
        e.printStackTrace(); // stack trace for detailed I/O error
        throw new ResourceException("Failed to read file: " + e.getMessage());
    }
}


    @PostMapping("/{id}/increment-download")
    public ResponseEntity<Void> incrementDownloadCount(@PathVariable Long id) {
        resourceService.incrementDownloadCount(id);
        return ResponseEntity.ok().build();
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