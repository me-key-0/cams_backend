package com.cams.communication_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {
    private String type; // MESSAGE, TYPING, USER_JOINED, USER_LEFT, MESSAGE_READ, etc.
    private String roomId;
    private Object payload;
    private Long senderId;
    private String senderName;
    private String senderRole;
}