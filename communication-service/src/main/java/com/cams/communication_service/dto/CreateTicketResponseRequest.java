package com.cams.communication_service.dto;

import com.cams.communication_service.model.Ticket;
import lombok.Data;

@Data
public class CreateTicketResponseRequest {
    private String message;
    private Ticket.Status newStatus; // Optional: to update ticket status when responding
}