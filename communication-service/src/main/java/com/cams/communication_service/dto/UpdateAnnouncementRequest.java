package com.cams.communication_service.dto;

import com.cams.communication_service.model.Announcement;
import lombok.Data;

@Data
public class UpdateAnnouncementRequest {
    private String title;
    private String content;
    private Announcement.Category category;
}