package com.cams.communication_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AnnouncementResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String createdBy;
    private String role;
    private String departmentCode;
    private boolean isGlobal;
}
