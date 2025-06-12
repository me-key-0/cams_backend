package com.cams.communication_service.service.impl;

import com.cams.communication_service.client.CourseServiceClient;
import com.cams.communication_service.client.CourseSessionDto;
import com.cams.communication_service.dto.*;
import com.cams.communication_service.model.ChatMessage;
import com.cams.communication_service.model.ChatParticipant;
import com.cams.communication_service.model.ChatRoom;
import com.cams.communication_service.repository.ChatMessageRepository;
import com.cams.communication_service.repository.ChatParticipantRepository;
import com.cams.communication_service.repository.ChatRoomRepository;
import com.cams.communication_service.service.ChatService;
import com.cams.communication_service.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final CourseServiceClient courseServiceClient;
    private final WebSocketService webSocketService;

    @Override
    @Transactional
    public ChatRoomDto createOrGetChatRoom(Long courseSessionId, Long studentId, String studentName, 
                                          Long lecturerId, String lecturerName) {
        // Check if chat room already exists
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByCourseSessionAndParticipants(
            courseSessionId, studentId, lecturerId);
        
        if (existingRoom.isPresent()) {
            return convertToDto(existingRoom.get(), studentId);
        }
        
        // Create new chat room
        String roomId = generateRoomId(courseSessionId, studentId, lecturerId);
        
        ChatRoom chatRoom = ChatRoom.builder()
            .roomId(roomId)
            .courseSessionId(courseSessionId)
            .studentId(studentId)
            .studentName(studentName)
            .lecturerId(lecturerId)
            .lecturerName(lecturerName)
            .isActive(true)
            .build();
        
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
        
        // Create participants
        createParticipant(savedRoom, studentId, studentName, "STUDENT");
        createParticipant(savedRoom, lecturerId, lecturerName, "LECTURER");
        
        log.info("Created chat room: {} for course session: {}", roomId, courseSessionId);
        return convertToDto(savedRoom, studentId);
    }

    @Override
    public ChatRoomDto getChatRoom(String roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Chat room not found"));
        
        // Verify user has access to this chat room
        if (!chatRoom.getStudentId().equals(userId) && !chatRoom.getLecturerId().equals(userId)) {
            throw new RuntimeException("Access denied to chat room");
        }
        
        return convertToDto(chatRoom, userId);
    }

    @Override
    public List<ChatRoomDto> getUserChatRooms(Long userId, String userRole) {
        List<ChatRoom> chatRooms;
        
        if ("STUDENT".equals(userRole)) {
            // Get student's course sessions first
            List<CourseSessionDto> courseSessions = courseServiceClient.getCourseSessionByStudentId(userId);
            List<Long> courseSessionIds = courseSessions.stream()
                .map(CourseSessionDto::getId)
                .collect(Collectors.toList());
            
            chatRooms = chatRoomRepository.findByStudentAndCourseSessionsOrderByLastActivity(userId, courseSessionIds);
        } else if ("LECTURER".equals(userRole)) {
            chatRooms = chatRoomRepository.findByLecturerIdAndActiveOrderByLastActivity(userId);
        } else {
            throw new RuntimeException("Invalid user role for chat access");
        }
        
        return chatRooms.stream()
            .map(room -> convertToDto(room, userId))
            .collect(Collectors.toList());
    }

    @Override
    public List<ChatRoomDto> getLecturerChatRoomsForCourse(Long lecturerId, Long courseSessionId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByCourseSessionAndLecturerOrderByLastActivity(
            courseSessionId, lecturerId);
        
        return chatRooms.stream()
            .map(room -> convertToDto(room, lecturerId))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatMessageDto sendMessage(SendMessageRequest request, Long senderId, String senderName, String senderRole) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(request.getRoomId())
            .orElseThrow(() -> new RuntimeException("Chat room not found"));
        
        // Verify sender has access to this chat room
        if (!chatRoom.getStudentId().equals(senderId) && !chatRoom.getLecturerId().equals(senderId)) {
            throw new RuntimeException("Access denied to chat room");
        }
        
        ChatMessage message = ChatMessage.builder()
            .chatRoom(chatRoom)
            .senderId(senderId)
            .senderName(senderName)
            .senderRole(senderRole)
            .content(request.getContent())
            .messageType(request.getMessageType())
            .isRead(false)
            .isDelivered(false)
            .build();
        
        ChatMessage savedMessage = chatMessageRepository.save(message);
        
        // Update chat room last activity
        chatRoom.setLastActivity(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);
        
        ChatMessageDto messageDto = convertToMessageDto(savedMessage);
        
        // Send message via WebSocket to all participants
        webSocketService.sendMessageToRoom(request.getRoomId(), messageDto);
        
        log.info("Message sent in room: {} by user: {}", request.getRoomId(), senderId);
        return messageDto;
    }

    @Override
    public Page<ChatMessageDto> getChatHistory(String roomId, Long userId, int page, int size) {
        // Verify user has access to this chat room
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Chat room not found"));
        
        if (!chatRoom.getStudentId().equals(userId) && !chatRoom.getLecturerId().equals(userId)) {
            throw new RuntimeException("Access denied to chat room");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderBySentAtDesc(roomId, pageable);
        
        return messages.map(this::convertToMessageDto);
    }

    @Override
    public List<ChatMessageDto> getRecentMessages(String roomId, Long userId, int limit) {
        Page<ChatMessageDto> messages = getChatHistory(roomId, userId, 0, limit);
        return messages.getContent();
    }

    @Override
    @Transactional
    public void markMessagesAsRead(String roomId, Long userId) {
        List<ChatMessage> unreadMessages = chatMessageRepository.findUnreadMessagesInRoom(roomId, userId);
        
        for (ChatMessage message : unreadMessages) {
            message.setRead(true);
        }
        
        if (!unreadMessages.isEmpty()) {
            chatMessageRepository.saveAll(unreadMessages);
            
            // Notify sender that messages were read
            webSocketService.sendReadReceipt(roomId, userId, unreadMessages.size());
            log.info("Marked {} messages as read in room: {} by user: {}", unreadMessages.size(), roomId, userId);
        }
    }

    @Override
    @Transactional
    public void markMessageAsDelivered(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("Message not found"));
        
        message.setDelivered(true);
        chatMessageRepository.save(message);
    }

    @Override
    public int getUnreadMessageCount(String roomId, Long userId) {
        return chatMessageRepository.countUnreadMessagesInRoom(roomId, userId);
    }

    @Override
    @Transactional
    public void joinChatRoom(String roomId, Long userId, String userName, String userRole) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Chat room not found"));
        
        Optional<ChatParticipant> existingParticipant = chatParticipantRepository.findByRoomIdAndUserId(roomId, userId);
        
        if (existingParticipant.isPresent()) {
            // Update online status
            ChatParticipant participant = existingParticipant.get();
            participant.setOnline(true);
            participant.setLastSeenAt(LocalDateTime.now());
            chatParticipantRepository.save(participant);
        } else {
            // Create new participant
            createParticipant(chatRoom, userId, userName, userRole);
        }
        
        // Notify other participants
        webSocketService.sendUserJoinedNotification(roomId, userId, userName, userRole);
        log.info("User {} joined chat room: {}", userId, roomId);
    }

    @Override
    @Transactional
    public void leaveChatRoom(String roomId, Long userId) {
        Optional<ChatParticipant> participant = chatParticipantRepository.findByRoomIdAndUserId(roomId, userId);
        
        if (participant.isPresent()) {
            ChatParticipant p = participant.get();
            p.setOnline(false);
            p.setLastSeenAt(LocalDateTime.now());
            chatParticipantRepository.save(p);
            
            // Notify other participants
            webSocketService.sendUserLeftNotification(roomId, userId, p.getUserName(), p.getUserRole());
            log.info("User {} left chat room: {}", userId, roomId);
        }
    }

    @Override
    @Transactional
    public void updateUserOnlineStatus(Long userId, boolean isOnline) {
        List<ChatParticipant> participants = chatParticipantRepository.findByUserId(userId);
        
        for (ChatParticipant participant : participants) {
            participant.setOnline(isOnline);
            if (!isOnline) {
                participant.setLastSeenAt(LocalDateTime.now());
            }
        }
        
        if (!participants.isEmpty()) {
            chatParticipantRepository.saveAll(participants);
        }
    }

    @Override
    public List<ChatParticipantDto> getChatRoomParticipants(String roomId) {
        List<ChatParticipant> participants = chatParticipantRepository.findByRoomId(roomId);
        
        return participants.stream()
            .map(this::convertToParticipantDto)
            .collect(Collectors.toList());
    }

    @Override
    public void sendTypingIndicator(String roomId, Long userId, String userName, String userRole, boolean isTyping) {
        // Verify user has access to this chat room
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Chat room not found"));
        
        if (!chatRoom.getStudentId().equals(userId) && !chatRoom.getLecturerId().equals(userId)) {
            throw new RuntimeException("Access denied to chat room");
        }
        
        webSocketService.sendTypingIndicator(roomId, userId, userName, userRole, isTyping);
    }

    // Helper methods
    private String generateRoomId(Long courseSessionId, Long studentId, Long lecturerId) {
        return String.format("%d_%d_%d", courseSessionId, studentId, lecturerId);
    }

    private void createParticipant(ChatRoom chatRoom, Long userId, String userName, String userRole) {
        ChatParticipant participant = ChatParticipant.builder()
            .chatRoom(chatRoom)
            .userId(userId)
            .userName(userName)
            .userRole(userRole)
            .isOnline(false)
            .build();
        
        chatParticipantRepository.save(participant);
    }

    private ChatRoomDto convertToDto(ChatRoom chatRoom, Long currentUserId) {
        ChatRoomDto dto = ChatRoomDto.builder()
            .id(chatRoom.getId())
            .roomId(chatRoom.getRoomId())
            .courseSessionId(chatRoom.getCourseSessionId())
            .studentId(chatRoom.getStudentId())
            .studentName(chatRoom.getStudentName())
            .lecturerId(chatRoom.getLecturerId())
            .lecturerName(chatRoom.getLecturerName())
            .createdAt(chatRoom.getCreatedAt())
            .lastActivity(chatRoom.getLastActivity())
            .isActive(chatRoom.isActive())
            .build();
        
        // Get unread message count
        dto.setUnreadCount(getUnreadMessageCount(chatRoom.getRoomId(), currentUserId));
        
        // Get last message
        ChatMessage lastMessage = chatMessageRepository.findLastMessageInRoom(chatRoom.getRoomId());
        if (lastMessage != null) {
            dto.setLastMessage(convertToMessageDto(lastMessage));
        }
        
        // Get participants
        dto.setParticipants(getChatRoomParticipants(chatRoom.getRoomId()));
        
        return dto;
    }

    private ChatMessageDto convertToMessageDto(ChatMessage message) {
        return ChatMessageDto.builder()
            .id(message.getId())
            .roomId(message.getChatRoom().getRoomId())
            .senderId(message.getSenderId())
            .senderName(message.getSenderName())
            .senderRole(message.getSenderRole())
            .content(message.getContent())
            .messageType(message.getMessageType())
            .sentAt(message.getSentAt())
            .isRead(message.isRead())
            .isDelivered(message.isDelivered())
            .build();
    }

    private ChatParticipantDto convertToParticipantDto(ChatParticipant participant) {
        return ChatParticipantDto.builder()
            .id(participant.getId())
            .userId(participant.getUserId())
            .userName(participant.getUserName())
            .userRole(participant.getUserRole())
            .joinedAt(participant.getJoinedAt())
            .lastSeenAt(participant.getLastSeenAt())
            .isOnline(participant.isOnline())
            .build();
    }
}