package com.cams.communication_service.service;

import com.cams.communication_service.dto.ChatMessageDto;

public interface WebSocketService {
    
    void sendMessageToRoom(String roomId, ChatMessageDto message);
    void sendTypingIndicator(String roomId, Long userId, String userName, String userRole, boolean isTyping);
    void sendUserJoinedNotification(String roomId, Long userId, String userName, String userRole);
    void sendUserLeftNotification(String roomId, Long userId, String userName, String userRole);
    void sendReadReceipt(String roomId, Long userId, int messageCount);
    void addUserSession(Long userId, String sessionId);
    void removeUserSession(String sessionId);
    void sendToUser(Long userId, Object message);
}