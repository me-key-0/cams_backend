package com.cams.communication_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class NotificationStatsResponse {
    private int totalNotifications;
    private int unreadCount;
    private List<NotificationResponse> notifications;
}