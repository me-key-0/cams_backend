package com.cams.communication_service.controller;

import com.cams.communication_service.dto.*;
import com.cams.communication_service.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/com/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // Chat Room Management
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomDto> createChatRoom(
            @RequestParam Long courseSessionId,
            @RequestParam Long studentId,
            @RequestParam String studentName,
            @RequestParam Long lecturerId,
            @RequestParam String lecturerName,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        // Validate that the requesting user is either the student or lecturer
        Long requestingUserId = Long.parseLong(userId);
        if (!requestingUserId.equals(studentId) && !requestingUserId.equals(lecturerId)) {
            return ResponseEntity.status(403).build();
        }
        
        ChatRoomDto chatRoom = chatService.createOrGetChatRoom(courseSessionId, studentId, studentName, lecturerId, lecturerName);
        return ResponseEntity.ok(chatRoom);
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ChatRoomDto> getChatRoom(
            @PathVariable String roomId,
            @RequestHeader("X-User-Id") String userId) {
        
        ChatRoomDto chatRoom = chatService.getChatRoom(roomId, Long.parseLong(userId));
        return ResponseEntity.ok(chatRoom);
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDto>> getUserChatRooms(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        List<ChatRoomDto> chatRooms = chatService.getUserChatRooms(Long.parseLong(userId), role);
        return ResponseEntity.ok(chatRooms);
    }

    @GetMapping("/rooms/course/{courseSessionId}")
    public ResponseEntity<List<ChatRoomDto>> getLecturerChatRoomsForCourse(
            @PathVariable Long courseSessionId,
            @RequestHeader("X-User-Id") String lecturerId,
            @RequestHeader("X-User-Role") String role) {
        
        if (!"LECTURER".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        
        List<ChatRoomDto> chatRooms = chatService.getLecturerChatRoomsForCourse(Long.parseLong(lecturerId), courseSessionId);
        return ResponseEntity.ok(chatRooms);
    }

    // Message Management
    @PostMapping("/messages")
    public ResponseEntity<ChatMessageDto> sendMessage(
            @RequestBody SendMessageRequest request,
            @RequestHeader("X-User-Id") String senderId,
            @RequestHeader("X-User-Role") String role) {
        
        String senderName = "User " + senderId; // Should be fetched from user service
        ChatMessageDto message = chatService.sendMessage(request, Long.parseLong(senderId), senderName, role);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<Page<ChatMessageDto>> getChatHistory(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("X-User-Id") String userId) {
        
        Page<ChatMessageDto> messages = chatService.getChatHistory(roomId, Long.parseLong(userId), page, size);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/rooms/{roomId}/messages/recent")
    public ResponseEntity<List<ChatMessageDto>> getRecentMessages(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "50") int limit,
            @RequestHeader("X-User-Id") String userId) {
        
        List<ChatMessageDto> messages = chatService.getRecentMessages(roomId, Long.parseLong(userId), limit);
        return ResponseEntity.ok(messages);
    }

    // Message Status
    @PostMapping("/rooms/{roomId}/mark-read")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable String roomId,
            @RequestHeader("X-User-Id") String userId) {
        
        chatService.markMessagesAsRead(roomId, Long.parseLong(userId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rooms/{roomId}/unread-count")
    public ResponseEntity<Integer> getUnreadMessageCount(
            @PathVariable String roomId,
            @RequestHeader("X-User-Id") String userId) {
        
        int count = chatService.getUnreadMessageCount(roomId, Long.parseLong(userId));
        return ResponseEntity.ok(count);
    }

    // Participant Management
    @GetMapping("/rooms/{roomId}/participants")
    public ResponseEntity<List<ChatParticipantDto>> getChatRoomParticipants(
            @PathVariable String roomId) {
        
        List<ChatParticipantDto> participants = chatService.getChatRoomParticipants(roomId);
        return ResponseEntity.ok(participants);
    }

    @PostMapping("/rooms/{roomId}/join")
    public ResponseEntity<Void> joinChatRoom(
            @PathVariable String roomId,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        String userName = "User " + userId; // Should be fetched from user service
        chatService.joinChatRoom(roomId, Long.parseLong(userId), userName, role);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rooms/{roomId}/leave")
    public ResponseEntity<Void> leaveChatRoom(
            @PathVariable String roomId,
            @RequestHeader("X-User-Id") String userId) {
        
        chatService.leaveChatRoom(roomId, Long.parseLong(userId));
        return ResponseEntity.ok().build();
    }

    // Typing Indicators
    @PostMapping("/rooms/{roomId}/typing")
    public ResponseEntity<Void> sendTypingIndicator(
            @PathVariable String roomId,
            @RequestParam boolean isTyping,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        
        String userName = "User " + userId; // Should be fetched from user service
        chatService.sendTypingIndicator(roomId, Long.parseLong(userId), userName, role, isTyping);
        return ResponseEntity.ok().build();
    }
}