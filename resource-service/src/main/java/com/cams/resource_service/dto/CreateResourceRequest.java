package com.cams.resource_service.dto;

import com.cams.resource_service.model.enums.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateResourceRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Resource type is required")
    private ResourceType type;

    @NotNull(message = "Course session ID is required")
    private Long courseSessionId;

    @NotNull(message = "Uploader ID is required")
    private Long uploadedBy;

    @Size(min = 1, message = "At least one category is required")
    private List<String> categories;
} 