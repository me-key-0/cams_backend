package com.cams.communication_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long id;
    private String subject;
    private String message;
    private String type;
    private Long courseSessionId;
    private String lecturerId;
    private String lecturerName;
    private LocalDateTime createdAt;
    private boolean active;
    private boolean isRead; // For student view - indicates if current student has read it
}