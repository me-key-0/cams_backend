package com.cams.communication_service.dto;

import lombok.Data;

@Data
public class CreateNotificationRequest {
    private String subject;
    private String message;
    private String type; // deadline, reminder, or custom type
    private Long courseSessionId;
}