package com.cams.resource_service.dto;

import com.cams.resource_service.model.ResourceMaterial;
import com.cams.resource_service.model.enums.ResourceType;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceDtoConverter {
    
    public static ResourceResponse toResponse(ResourceMaterial resource) {
        ResourceResponse response = new ResourceResponse();
        response.setId(resource.getId());
        response.setTitle(resource.getTitle());
        response.setDescription(resource.getDescription());
        response.setFileName(resource.getFileName());
        response.setOriginalFileName(resource.getOriginalFileName());
        response.setType(resource.getType());
        response.setFileSize(resource.getFileSize());
        response.setMimeType(resource.getMimeType());
        response.setCategories(resource.getCategories());
        response.setDownloadCount(resource.getDownloadCount());
        response.setUploadedAt(resource.getUploadedAt());
        response.setCourseSessionId(resource.getCourseSessionId());
        response.setUploadedBy(resource.getUploadedBy());
        response.setUploaderName(resource.getUploaderName());
        response.setStatus(resource.getStatus());
        response.setLinkUrl(resource.getLinkUrl());
        
        // Generate download URL for non-link resources
        if (resource.getType() != ResourceType.LINK) {
            response.setDownloadUrl("/api/v1/resources/download/" + resource.getFileName() + "/" + resource.getId());
        }
        
        // Format file size
        response.setFileSizeFormatted(formatFileSize(resource.getFileSize()));
        
        return response;
    }

    public static List<ResourceResponse> toResponseList(List<ResourceMaterial> resources) {
        return resources.stream()
            .map(ResourceDtoConverter::toResponse)
            .collect(Collectors.toList());
    }

    private static String formatFileSize(Long bytes) {
        if (bytes == null || bytes == 0) return "0 B";
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes.doubleValue();
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(size) + " " + units[unitIndex];
    }
}