package com.cams.communication_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {
    private Long id;
    private String roomId;
    private Long courseSessionId;
    private Long studentId;
    private String studentName;
    private Long lecturerId;
    private String lecturerName;
    private LocalDateTime createdAt;
    private LocalDateTime lastActivity;
    private boolean isActive;
    private int unreadCount;
    private ChatMessageDto lastMessage;
    private List<ChatParticipantDto> participants;
}