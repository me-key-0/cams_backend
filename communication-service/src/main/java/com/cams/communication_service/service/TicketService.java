package com.cams.communication_service.service;

import com.cams.communication_service.dto.CreateTicketRequest;
import com.cams.communication_service.dto.CreateTicketResponseRequest;
import com.cams.communication_service.dto.TicketDtoConverter;
import com.cams.communication_service.dto.TicketResponse;
import com.cams.communication_service.model.Ticket;
import com.cams.communication_service.repository.TicketRepository;
import com.cams.communication_service.repository.TicketResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private TicketResponseRepository ticketResponseRepository;

    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request, String senderId, String senderRole, 
                                     String senderName, String departmentCode) {
        
        Ticket ticket = new Ticket();
        ticket.setSubject(request.getSubject());
        ticket.setMessage(request.getMessage());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(Ticket.Status.OPEN);
        ticket.setSenderId(senderId);
        ticket.setSenderRole(senderRole);
        ticket.setSenderName(senderName);
        ticket.setDepartmentCode(departmentCode);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setRead(false);
        
        Ticket savedTicket = ticketRepository.save(ticket);
        return TicketDtoConverter.toResponse(savedTicket);
    }

    public List<TicketResponse> getTicketsBySender(String senderId) {
        List<Ticket> tickets = ticketRepository.findBySenderIdOrderByCreatedAtDesc(senderId);
        return TicketDtoConverter.toResponseList(tickets);
    }

    public List<TicketResponse> getAllTicketsForAdmin() {
        List<Ticket> tickets = ticketRepository.findAllByOrderByCreatedAtDesc();
        return TicketDtoConverter.toResponseList(tickets);
    }

    public List<TicketResponse> getFilteredTickets(Ticket.Priority priority, String senderRole, 
                                                 Ticket.Status status, Boolean isRead) {
        List<Ticket> tickets = ticketRepository.findTicketsWithFilters(priority, senderRole, status, isRead);
        return TicketDtoConverter.toResponseList(tickets);
    }

    @Transactional
    public TicketResponse respondToTicket(Long ticketId, CreateTicketResponseRequest request, 
                                        String responderId, String responderName) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        // Create response
        com.cams.communication_service.model.TicketResponse response = 
            new com.cams.communication_service.model.TicketResponse();
        response.setMessage(request.getMessage());
        response.setResponderId(responderId);
        response.setResponderName(responderName);
        response.setTicket(ticket);
        response.setCreatedAt(LocalDateTime.now());
        
        ticketResponseRepository.save(response);

        // Update ticket status if provided
        if (request.getNewStatus() != null) {
            ticket.setStatus(request.getNewStatus());
        } else {
            // Default to IN_PROGRESS if no status specified
            ticket.setStatus(Ticket.Status.IN_PROGRESS);
        }
        
        ticketRepository.save(ticket);
        
        // Return updated ticket
        Ticket updatedTicket = ticketRepository.findById(ticketId).orElseThrow();
        return TicketDtoConverter.toResponse(updatedTicket);
    }

    @Transactional
    public void markTicketAsRead(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        ticket.setRead(true);
        ticketRepository.save(ticket);
    }

    @Transactional
    public void deleteTicket(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new IllegalArgumentException("Ticket not found");
        }
        ticketRepository.deleteById(ticketId);
    }

    @Transactional
    public TicketResponse updateTicketStatus(Long ticketId, Ticket.Status status) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        ticket.setStatus(status);
        Ticket updatedTicket = ticketRepository.save(ticket);
        return TicketDtoConverter.toResponse(updatedTicket);
    }

    public TicketResponse getTicketById(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        return TicketDtoConverter.toResponse(ticket);
    }
}