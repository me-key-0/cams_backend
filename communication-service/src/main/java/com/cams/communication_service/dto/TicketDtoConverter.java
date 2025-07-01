package com.cams.communication_service.dto;

import com.cams.communication_service.model.Ticket;
import com.cams.communication_service.model.TicketResponse;
import java.util.List;
import java.util.stream.Collectors;

public class TicketDtoConverter {
    
    public static com.cams.communication_service.dto.TicketResponse toResponse(Ticket ticket) {
        com.cams.communication_service.dto.TicketResponse response = new com.cams.communication_service.dto.TicketResponse();
        response.setId(ticket.getId());
        response.setSubject(ticket.getSubject());
        response.setMessage(ticket.getMessage());
        response.setPriority(ticket.getPriority());
        response.setStatus(ticket.getStatus());
        response.setSenderRole(ticket.getSenderRole());
        response.setSenderId(ticket.getSenderId());
        response.setSenderName(ticket.getSenderName());
        response.setDepartmentCode(ticket.getDepartmentCode());
        response.setCreatedAt(ticket.getCreatedAt());
        response.setRead(ticket.isRead());
        
        if (ticket.getResponses() != null) {
            response.setResponses(ticket.getResponses().stream()
                .map(TicketDtoConverter::toResponseDto)
                .collect(Collectors.toList()));
        }
        
        return response;
    }

    public static TicketResponseDto toResponseDto(TicketResponse ticketResponse) {
        TicketResponseDto dto = new TicketResponseDto();
        dto.setId(ticketResponse.getId());
        dto.setMessage(ticketResponse.getMessage());
        dto.setResponderId(ticketResponse.getResponderId());
        dto.setResponderName(ticketResponse.getResponderName());
        dto.setCreatedAt(ticketResponse.getCreatedAt());
        return dto;
    }

    public static List<com.cams.communication_service.dto.TicketResponse> toResponseList(List<Ticket> tickets) {
        return tickets.stream()
            .map(TicketDtoConverter::toResponse)
            .collect(Collectors.toList());
    }
}