package com.cams.communication_service.dto;

import com.cams.communication_service.model.Announcement;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AnnouncementResponse {
    private Long id;
    private String title;
    private String content;
    private Announcement.Category category;
    private LocalDateTime createdAt;
    private String createdBy;
    private String createdByName;
    private String role;
    private String departmentCode;
    private boolean isGlobal;
    private boolean active;
    private boolean isRead; // For user view - indicates if current user has read it
}