package com.cams.communication_service.dto;

import com.cams.communication_service.model.ChatMessage;
import lombok.Data;

@Data
public class SendMessageRequest {
    private String roomId;
    private String content;
    private ChatMessage.MessageType messageType = ChatMessage.MessageType.TEXT;
}