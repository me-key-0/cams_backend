package com.cams.resource_service.model.enums;

/**
 * Enum representing the status of a resource.
 */
public enum ResourceStatus {
    ACTIVE,     // Resource is available for viewing/download
    ARCHIVED,   // Resource is archived but can be restored
    DELETED     // Resource is marked for deletion
} 