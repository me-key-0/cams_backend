package com.cams.communication_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class AnnouncementStatsResponse {
    private int totalAnnouncements;
    private int unreadCount;
    private List<AnnouncementResponse> announcements;
}