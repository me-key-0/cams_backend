package com.cams.communication_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketResponseDto {
    private Long id;
    private String message;
    private String responderId;
    private String responderName;
    private LocalDateTime createdAt;
}