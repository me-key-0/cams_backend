package com.cams.communication_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatParticipantDto {
    private Long id;
    private Long userId;
    private String userName;
    private String userRole;
    private LocalDateTime joinedAt;
    private LocalDateTime lastSeenAt;
    private boolean isOnline;
}