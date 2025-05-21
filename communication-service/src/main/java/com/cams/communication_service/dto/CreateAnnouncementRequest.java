package com.cams.communication_service.dto;

import lombok.Data;

@Data
public class CreateAnnouncementRequest {
    private String title;
    private String content;
    private String departmentCode; // Only for department-specific announcements
}
