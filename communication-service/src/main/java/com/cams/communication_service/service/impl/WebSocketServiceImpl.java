package com.cams.communication_service.service.impl;

import com.cams.communication_service.dto.ChatMessageDto;
import com.cams.communication_service.dto.WebSocketMessage;
import com.cams.communication_service.service.WebSocketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

    private final ObjectMapper objectMapper;
    
    // Map to store user sessions: userId -> Set of WebSocket sessions
    private final Map<Long, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();
    
    // Map to store room subscriptions: roomId -> Set of WebSocket sessions
    private final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    
    // Map to store session metadata: sessionId -> userId
    private final Map<String, Long> sessionUserMap = new ConcurrentHashMap<>();

    @Override
    public void sendMessageToRoom(String roomId, ChatMessageDto message) {
        WebSocketMessage wsMessage = WebSocketMessage.builder()
            .type("MESSAGE")
            .roomId(roomId)
            .payload(message)
            .senderId(message.getSenderId())
            .senderName(message.getSenderName())
            .senderRole(message.getSenderRole())
            .build();
        
        sendToRoom(roomId, wsMessage);
    }

    @Override
    public void sendTypingIndicator(String roomId, Long userId, String userName, String userRole, boolean isTyping) {
        WebSocketMessage wsMessage = WebSocketMessage.builder()
            .type("TYPING")
            .roomId(roomId)
            .payload(Map.of("isTyping", isTyping))
            .senderId(userId)
            .senderName(userName)
            .senderRole(userRole)
            .build();
        
        sendToRoom(roomId, wsMessage);
    }

    @Override
    public void sendUserJoinedNotification(String roomId, Long userId, String userName, String userRole) {
        WebSocketMessage wsMessage = WebSocketMessage.builder()
            .type("USER_JOINED")
            .roomId(roomId)
            .payload(Map.of("message", userName + " joined the chat"))
            .senderId(userId)
            .senderName(userName)
            .senderRole(userRole)
            .build();
        
        sendToRoom(roomId, wsMessage);
    }

    @Override
    public void sendUserLeftNotification(String roomId, Long userId, String userName, String userRole) {
        WebSocketMessage wsMessage = WebSocketMessage.builder()
            .type("USER_LEFT")
            .roomId(roomId)
            .payload(Map.of("message", userName + " left the chat"))
            .senderId(userId)
            .senderName(userName)
            .senderRole(userRole)
            .build();
        
        sendToRoom(roomId, wsMessage);
    }

    @Override
    public void sendReadReceipt(String roomId, Long userId, int messageCount) {
        WebSocketMessage wsMessage = WebSocketMessage.builder()
            .type("MESSAGE_READ")
            .roomId(roomId)
            .payload(Map.of("readBy", userId, "messageCount", messageCount))
            .build();
        
        sendToRoom(roomId, wsMessage);
    }

    @Override
    public void addUserSession(Long userId, String sessionId) {
        // This will be called from WebSocket handler when user connects
        log.debug("Adding user session: {} for user: {}", sessionId, userId);
        sessionUserMap.put(sessionId, userId);
    }

    @Override
    public void removeUserSession(String sessionId) {
        Long userId = sessionUserMap.remove(sessionId);
        if (userId != null) {
            Set<WebSocketSession> sessions = userSessions.get(userId);
            if (sessions != null) {
                sessions.removeIf(session -> session.getId().equals(sessionId));
                if (sessions.isEmpty()) {
                    userSessions.remove(userId);
                }
            }
            
            // Remove from all room subscriptions
            roomSessions.values().forEach(roomSessionSet -> 
                roomSessionSet.removeIf(session -> session.getId().equals(sessionId)));
            
            log.debug("Removed user session: {} for user: {}", sessionId, userId);
        }
    }

    @Override
    public void sendToUser(Long userId, Object message) {
        Set<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions != null && !sessions.isEmpty()) {
            String messageJson = convertToJson(message);
            sessions.forEach(session -> sendToSession(session, messageJson));
        }
    }

    public void addSessionToRoom(String roomId, WebSocketSession session) {
        roomSessions.computeIfAbsent(roomId, k -> new CopyOnWriteArraySet<>()).add(session);
        log.debug("Added session {} to room: {}", session.getId(), roomId);
    }

    public void removeSessionFromRoom(String roomId, WebSocketSession session) {
        Set<WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                roomSessions.remove(roomId);
            }
        }
        log.debug("Removed session {} from room: {}", session.getId(), roomId);
    }

    public void addUserWebSocketSession(Long userId, WebSocketSession session) {
        userSessions.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(session);
        sessionUserMap.put(session.getId(), userId);
        log.debug("Added WebSocket session for user: {}", userId);
    }

    private void sendToRoom(String roomId, WebSocketMessage message) {
        Set<WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions != null && !sessions.isEmpty()) {
            String messageJson = convertToJson(message);
            sessions.forEach(session -> sendToSession(session, messageJson));
            log.debug("Sent message to {} sessions in room: {}", sessions.size(), roomId);
        }
    }

    private void sendToSession(WebSocketSession session, String message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (IOException e) {
            log.error("Failed to send message to session: {}", session.getId(), e);
        }
    }

    private String convertToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Failed to convert object to JSON", e);
            return "{}";
        }
    }
}