package com.cams.resource_service.model.enums;

import java.util.Arrays;
import java.util.List;

public enum ResourceType {
    DOCUMENT("Document", Arrays.asList("pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt")),
    VIDEO("Video", Arrays.asList("mp4", "avi", "mov", "wmv", "flv", "webm", "mkv")),
    PHOTO("Photo", Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "svg", "webp")),
    LINK("Link", Arrays.asList()),
    AUDIO("Audio", Arrays.asList("mp3", "wav", "flac", "aac", "ogg")),
    ARCHIVE("Archive", Arrays.asList("zip", "rar", "7z", "tar", "gz"));

    private final String displayName;
    private final List<String> allowedExtensions;

    ResourceType(String displayName, List<String> allowedExtensions) {
        this.displayName = displayName;
        this.allowedExtensions = allowedExtensions;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getAllowedExtensions() {
        return allowedExtensions;
    }

    public static ResourceType fromFileExtension(String extension) {
        if (extension == null) return null;
        
        String ext = extension.toLowerCase().replaceFirst("^\\.", "");
        
        for (ResourceType type : values()) {
            if (type.getAllowedExtensions().contains(ext)) {
                return type;
            }
        }
        return DOCUMENT; // Default fallback
    }

    public boolean isValidExtension(String extension) {
        if (this == LINK) return true;
        if (extension == null) return false;
        
        String ext = extension.toLowerCase().replaceFirst("^\\.", "");
        return allowedExtensions.contains(ext);
    }
}