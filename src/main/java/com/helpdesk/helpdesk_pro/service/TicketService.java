package com.helpdesk.helpdesk_pro.service;

import com.helpdesk.helpdesk_pro.entity.Ticket;
import com.helpdesk.helpdesk_pro.entity.User;
import com.helpdesk.helpdesk_pro.enums.TicketPriority;
import com.helpdesk.helpdesk_pro.enums.TicketStatus;
import com.helpdesk.helpdesk_pro.repository.TicketRepository;
import com.helpdesk.helpdesk_pro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepo;
    private final UserRepository userRepo;

    public Page<Ticket> getAll(TicketStatus status,
                               TicketPriority priority,
                               Pageable pageable) {
        if (status != null)   return ticketRepo.findByStatus(status, pageable);
        if (priority != null) return ticketRepo.findByPriority(priority, pageable);
        return ticketRepo.findAll(pageable);
    }

    public Ticket findById(Long id) {
        return ticketRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ticket no encontrado: " + id));
    }

    public Ticket create(Ticket ticket, UserDetails userDetails) {
        User creator = userRepo.findByEmail(userDetails.getUsername())
                .orElseThrow();
        ticket.setCreatedBy(creator);
        ticket.setStatus(TicketStatus.NUEVO);
        ticket.setTicketNumber(generateNumber());
        return ticketRepo.save(ticket);
    }

    public Ticket update(Long id, Ticket data) {
        Ticket existing = findById(id);
        existing.setTitle(data.getTitle());
        existing.setDescription(data.getDescription());
        existing.setPriority(data.getPriority());
        existing.setTags(data.getTags());
        return ticketRepo.save(existing);
    }

    public Ticket updateStatus(Long id, TicketStatus status) {
        Ticket ticket = findById(id);
        ticket.setStatus(status);
        return ticketRepo.save(ticket);
    }

    public Ticket assign(Long ticketId, Long agentId) {
        Ticket ticket = findById(ticketId);
        User agent = userRepo.findById(agentId).orElseThrow();
        ticket.setAssignedTo(agent);
        ticket.setStatus(TicketStatus.ASIGNADO);
        return ticketRepo.save(ticket);
    }

    public void delete(Long id) {
        ticketRepo.delete(findById(id));
    }

    private String generateNumber() {
        return "TKT-" + Year.now().getValue() + "-"
                + String.format("%03d", ticketRepo.count() + 1);
    }
}