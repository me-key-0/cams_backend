package com.cams.resource_service.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ResourceStatsResponse {
    private int totalResources;
    private Map<String, Integer> resourcesByType;
    private Map<String, Integer> resourcesByCategory;
    private List<ResourceResponse> resources;
    private long totalFileSize;
    private String totalFileSizeFormatted;
}