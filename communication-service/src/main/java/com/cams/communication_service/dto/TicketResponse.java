package com.cams.communication_service.dto;

import com.cams.communication_service.model.Ticket;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TicketResponse {
    private Long id;
    private String subject;
    private String message;
    private Ticket.Priority priority;
    private Ticket.Status status;
    private String senderRole;
    private String senderId;
    private String senderName;
    private String departmentCode;
    private LocalDateTime createdAt;
    private boolean isRead;
    private List<TicketResponseDto> responses;
}