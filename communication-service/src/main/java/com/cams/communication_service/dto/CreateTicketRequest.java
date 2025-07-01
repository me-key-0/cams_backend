package com.cams.communication_service.dto;

import com.cams.communication_service.model.Ticket;
import lombok.Data;

@Data
public class CreateTicketRequest {
    private String subject;
    private String message;
    private Ticket.Priority priority;
}