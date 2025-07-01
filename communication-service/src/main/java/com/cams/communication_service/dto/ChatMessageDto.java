package com.cams.communication_service.dto;

import com.cams.communication_service.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private Long id;
    private String roomId;
    private Long senderId;
    private String senderName;
    private String senderRole;
    private String content;
    private ChatMessage.MessageType messageType;
    private LocalDateTime sentAt;
    private boolean isRead;
    private boolean isDelivered;
}