package com.helpdesk.helpdesk_pro.service;

import com.helpdesk.helpdesk_pro.dto.request.TicketCreateRequest;
import com.helpdesk.helpdesk_pro.entity.*;
import com.helpdesk.helpdesk_pro.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository    ticketRepository;
    private final UsuarioRepository   usuarioRepository;
    private final EstadoRepository    estadoRepository;
    private final PrioridadRepository prioridadRepository;

    public Page<Ticket> getAll(String estado, String prioridad, Pageable pageable) {
        if (estado    != null) return ticketRepository.findByEstadoNombre(estado, pageable);
        if (prioridad != null) return ticketRepository.findByPrioridadNombre(prioridad, pageable);
        return ticketRepository.findAll(pageable);
    }

    public Ticket findById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ticket no encontrado: " + id));
    }

    public Ticket create(TicketCreateRequest req, UserDetails userDetails) {
        Usuario cliente = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Estado estadoInicial = estadoRepository.findById(1L)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estado inicial no encontrado"));

        Prioridad prioridad = prioridadRepository.findById(req.getPrioridadId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Prioridad no encontrada"));

        Ticket ticket = new Ticket();
        ticket.setTitulo(req.getTitulo());
        ticket.setDescripcionInicial(req.getDescripcionInicial());
        ticket.setCliente(cliente);
        ticket.setEstado(estadoInicial);
        ticket.setPrioridad(prioridad);

        return ticketRepository.save(ticket);
    }

    public Ticket update(Long id, TicketCreateRequest req) {
        Ticket existing = findById(id);
        existing.setTitulo(req.getTitulo());
        existing.setDescripcionInicial(req.getDescripcionInicial());

        if (req.getPrioridadId() != null) {
            Prioridad prioridad = prioridadRepository.findById(req.getPrioridadId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Prioridad no encontrada"));
            existing.setPrioridad(prioridad);
        }
        return ticketRepository.save(existing);
    }

    public Ticket updateEstado(Long ticketId, Long estadoId) {
        Ticket ticket = findById(ticketId);
        Estado estado = estadoRepository.findById(estadoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estado no encontrado: " + estadoId));
        ticket.setEstado(estado);
        return ticketRepository.save(ticket);
    }

    public Ticket asignar(Long ticketId, Long agenteId) {
        Ticket ticket = findById(ticketId);
        Usuario agente = usuarioRepository.findById(agenteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Agente no encontrado: " + agenteId));
        ticket.setAgente(agente);

        // Cambia estado a "En Proceso" automáticamente al asignar
        estadoRepository.findByNombre("En Proceso")
                .ifPresent(ticket::setEstado);

        return ticketRepository.save(ticket);
    }

    public void delete(Long id) {
        ticketRepository.delete(findById(id));
    }
}