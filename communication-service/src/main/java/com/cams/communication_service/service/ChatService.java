package com.cams.communication_service.service;

import com.cams.communication_service.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ChatService {
    
    // Chat Room Management
    ChatRoomDto createOrGetChatRoom(Long courseSessionId, Long studentId, String studentName, 
                                   Long lecturerId, String lecturerName);
    ChatRoomDto getChatRoom(String roomId, Long userId);
    List<ChatRoomDto> getUserChatRooms(Long userId, String userRole);
    List<ChatRoomDto> getLecturerChatRoomsForCourse(Long lecturerId, Long courseSessionId);
    
    // Message Management
    ChatMessageDto sendMessage(SendMessageRequest request, Long senderId, String senderName, String senderRole);
    Page<ChatMessageDto> getChatHistory(String roomId, Long userId, int page, int size);
    List<ChatMessageDto> getRecentMessages(String roomId, Long userId, int limit);
    
    // Message Status
    void markMessagesAsRead(String roomId, Long userId);
    void markMessageAsDelivered(Long messageId);
    int getUnreadMessageCount(String roomId, Long userId);
    
    // Participant Management
    void joinChatRoom(String roomId, Long userId, String userName, String userRole);
    void leaveChatRoom(String roomId, Long userId);
    void updateUserOnlineStatus(Long userId, boolean isOnline);
    List<ChatParticipantDto> getChatRoomParticipants(String roomId);
    
    // Typing Indicators
    void sendTypingIndicator(String roomId, Long userId, String userName, String userRole, boolean isTyping);
}