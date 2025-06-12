package com.cams.communication_service.handler;

import com.cams.communication_service.dto.SendMessageRequest;
import com.cams.communication_service.service.ChatService;
import com.cams.communication_service.service.impl.WebSocketServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ChatService chatService;
    private final WebSocketServiceImpl webSocketService;
    private final ObjectMapper objectMapper;
    
    // Store session metadata
    private final Map<String, SessionInfo> sessionInfoMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket connection established: {}", session.getId());
        
        // Extract user info from query parameters
        URI uri = session.getUri();
        if (uri != null) {
            String query = uri.getQuery();
            if (query != null) {
                Map<String, String> params = parseQueryString(query);
                String userIdStr = params.get("userId");
                String userName = params.get("userName");
                String userRole = params.get("userRole");
                String roomId = params.get("roomId");
                
                if (userIdStr != null && userName != null && userRole != null) {
                    Long userId = Long.parseLong(userIdStr);
                    
                    SessionInfo sessionInfo = new SessionInfo(userId, userName, userRole, roomId);
                    sessionInfoMap.put(session.getId(), sessionInfo);
                    
                    // Add session to WebSocket service
                    webSocketService.addUserWebSocketSession(userId, session);
                    
                    // Join chat room if specified
                    if (roomId != null && !roomId.isEmpty()) {
                        webSocketService.addSessionToRoom(roomId, session);
                        chatService.joinChatRoom(roomId, userId, userName, userRole);
                    }
                    
                    log.info("User {} ({}) connected to WebSocket", userId, userName);
                }
            }
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage textMessage) {
            try {
                JsonNode jsonNode = objectMapper.readTree(textMessage.getPayload());
                String messageType = jsonNode.get("type").asText();
                
                SessionInfo sessionInfo = sessionInfoMap.get(session.getId());
                if (sessionInfo == null) {
                    log.warn("No session info found for session: {}", session.getId());
                    return;
                }
                
                switch (messageType) {
                    case "SEND_MESSAGE":
                        handleSendMessage(jsonNode, sessionInfo);
                        break;
                    case "JOIN_ROOM":
                        handleJoinRoom(jsonNode, sessionInfo, session);
                        break;
                    case "LEAVE_ROOM":
                        handleLeaveRoom(jsonNode, sessionInfo, session);
                        break;
                    case "TYPING":
                        handleTypingIndicator(jsonNode, sessionInfo);
                        break;
                    case "MARK_READ":
                        handleMarkRead(jsonNode, sessionInfo);
                        break;
                    default:
                        log.warn("Unknown message type: {}", messageType);
                }
            } catch (Exception e) {
                log.error("Error handling WebSocket message", e);
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket transport error for session: {}", session.getId(), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("WebSocket connection closed: {} with status: {}", session.getId(), closeStatus);
        
        SessionInfo sessionInfo = sessionInfoMap.remove(session.getId());
        if (sessionInfo != null) {
            // Leave current room
            if (sessionInfo.getCurrentRoomId() != null) {
                webSocketService.removeSessionFromRoom(sessionInfo.getCurrentRoomId(), session);
                chatService.leaveChatRoom(sessionInfo.getCurrentRoomId(), sessionInfo.getUserId());
            }
            
            // Update user online status
            chatService.updateUserOnlineStatus(sessionInfo.getUserId(), false);
        }
        
        // Remove session from WebSocket service
        webSocketService.removeUserSession(session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void handleSendMessage(JsonNode jsonNode, SessionInfo sessionInfo) {
        try {
            String roomId = jsonNode.get("roomId").asText();
            String content = jsonNode.get("content").asText();
            String messageType = jsonNode.has("messageType") ? 
                jsonNode.get("messageType").asText() : "TEXT";
            
            SendMessageRequest request = new SendMessageRequest();
            request.setRoomId(roomId);
            request.setContent(content);
            request.setMessageType(com.cams.communication_service.model.ChatMessage.MessageType.valueOf(messageType));
            
            chatService.sendMessage(request, sessionInfo.getUserId(), 
                sessionInfo.getUserName(), sessionInfo.getUserRole());
            
        } catch (Exception e) {
            log.error("Error handling send message", e);
        }
    }

    private void handleJoinRoom(JsonNode jsonNode, SessionInfo sessionInfo, WebSocketSession session) {
        try {
            String roomId = jsonNode.get("roomId").asText();
            
            // Leave current room if any
            if (sessionInfo.getCurrentRoomId() != null) {
                webSocketService.removeSessionFromRoom(sessionInfo.getCurrentRoomId(), session);
                chatService.leaveChatRoom(sessionInfo.getCurrentRoomId(), sessionInfo.getUserId());
            }
            
            // Join new room
            sessionInfo.setCurrentRoomId(roomId);
            webSocketService.addSessionToRoom(roomId, session);
            chatService.joinChatRoom(roomId, sessionInfo.getUserId(), 
                sessionInfo.getUserName(), sessionInfo.getUserRole());
            
        } catch (Exception e) {
            log.error("Error handling join room", e);
        }
    }

    private void handleLeaveRoom(JsonNode jsonNode, SessionInfo sessionInfo, WebSocketSession session) {
        try {
            String roomId = jsonNode.get("roomId").asText();
            
            if (roomId.equals(sessionInfo.getCurrentRoomId())) {
                webSocketService.removeSessionFromRoom(roomId, session);
                chatService.leaveChatRoom(roomId, sessionInfo.getUserId());
                sessionInfo.setCurrentRoomId(null);
            }
            
        } catch (Exception e) {
            log.error("Error handling leave room", e);
        }
    }

    private void handleTypingIndicator(JsonNode jsonNode, SessionInfo sessionInfo) {
        try {
            String roomId = jsonNode.get("roomId").asText();
            boolean isTyping = jsonNode.get("isTyping").asBoolean();
            
            chatService.sendTypingIndicator(roomId, sessionInfo.getUserId(), 
                sessionInfo.getUserName(), sessionInfo.getUserRole(), isTyping);
            
        } catch (Exception e) {
            log.error("Error handling typing indicator", e);
        }
    }

    private void handleMarkRead(JsonNode jsonNode, SessionInfo sessionInfo) {
        try {
            String roomId = jsonNode.get("roomId").asText();
            chatService.markMessagesAsRead(roomId, sessionInfo.getUserId());
            
        } catch (Exception e) {
            log.error("Error handling mark read", e);
        }
    }

    private Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new ConcurrentHashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }

    private static class SessionInfo {
        private final Long userId;
        private final String userName;
        private final String userRole;
        private String currentRoomId;

        public SessionInfo(Long userId, String userName, String userRole, String currentRoomId) {
            this.userId = userId;
            this.userName = userName;
            this.userRole = userRole;
            this.currentRoomId = currentRoomId;
        }

        public Long getUserId() { return userId; }
        public String getUserName() { return userName; }
        public String getUserRole() { return userRole; }
        public String getCurrentRoomId() { return currentRoomId; }
        public void setCurrentRoomId(String currentRoomId) { this.currentRoomId = currentRoomId; }
    }
}