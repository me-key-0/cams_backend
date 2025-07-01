package com.cams.communication_service.dto;

import com.cams.communication_service.model.Announcement;
import lombok.Data;

@Data
public class CreateAnnouncementRequest {
    private String title;
    private String content;
    private Announcement.Category category;
    private String departmentCode; // Only for department-specific announcements
}