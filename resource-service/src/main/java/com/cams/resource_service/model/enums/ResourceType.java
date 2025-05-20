package com.cams.resource_service.model.enums;

/**
 * Enum representing different types of resources that can be uploaded.
 */
public enum ResourceType {
    DOCUMENT("application/octet-stream"),    // PDF, Word, Excel, PowerPoint
    VIDEO("video/mp4"),       // Video files
    PHOTO("image/jpeg"),       // Image files
    LINK("text/html"),        // External URLs
    FOLDER("application/x-directory");       // Collection of resources

    private final String mimeType;

    ResourceType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }
}