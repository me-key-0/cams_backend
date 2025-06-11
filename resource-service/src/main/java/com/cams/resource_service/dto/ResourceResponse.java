package com.cams.resource_service.dto;

import com.cams.resource_service.model.enums.ResourceStatus;
import com.cams.resource_service.model.enums.ResourceType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ResourceResponse {
    private Long id;
    private String title;
    private String description;
    private String fileName;
    private String originalFileName;
    private ResourceType type;
    private Long fileSize;
    private String mimeType;
    private Set<String> categories;
    private Integer downloadCount;
    private LocalDateTime uploadedAt;
    private Long courseSessionId;
    private Long uploadedBy;
    private String uploaderName;
    private ResourceStatus status;
    private String linkUrl;
    private String downloadUrl; // Generated download URL
    private String fileSizeFormatted; // Human readable file size
}