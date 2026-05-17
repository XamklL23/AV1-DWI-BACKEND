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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository    ticketRepository;
    private final UsuarioRepository   usuarioRepository;
    private final EstadoRepository    estadoRepository;
    private final PrioridadRepository prioridadRepository;
    private final BitacoraRepository  bitacoraRepository;

    @Transactional(readOnly = true)
    public Page<Ticket> getAll(String estado, String prioridad, Pageable pageable) {
        if (estado    != null) return ticketRepository.findByEstadoNombre(estado, pageable);
        if (prioridad != null) return ticketRepository.findByPrioridadNombre(prioridad, pageable);
        return ticketRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Ticket findById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ticket no encontrado: " + id));
    }

    @Transactional
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

        Ticket saved = ticketRepository.save(ticket);

        registrarBitacora(saved, null, estadoInicial, cliente, "Ticket creado");

        return saved;
    }

    @Transactional
    public Ticket update(Long id, TicketCreateRequest req, UserDetails userDetails) {
        Ticket existing = findById(id);
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow();

        existing.setTitulo(req.getTitulo());
        existing.setDescripcionInicial(req.getDescripcionInicial());

        if (req.getPrioridadId() != null) {
            Prioridad prioridad = prioridadRepository.findById(req.getPrioridadId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Prioridad no encontrada"));
            existing.setPrioridad(prioridad);
        }

        Ticket saved = ticketRepository.save(existing);

        // Segunda operación en BD — necesita @Transactional
        registrarBitacora(saved, saved.getEstado(), saved.getEstado(),
                usuario, "Ticket actualizado: " + saved.getTitulo());

        return saved;
    }

    @Transactional
    public Ticket updateEstado(Long ticketId,
                               Long estadoId,
                               UserDetails userDetails) {

        Ticket ticket = findById(ticketId);

        Estado estadoAnterior = ticket.getEstado();

        Estado estadoNuevo = estadoRepository.findById(estadoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Estado no encontrado: " + estadoId));

        Usuario usuario = usuarioRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow();

        // ── ASIGNAR AGENTE AUTOMÁTICAMENTE ──
        if (
                usuario.getRol().name().equals("AGENTE") ||
                        usuario.getRol().name().equals("ADMIN")
        ) {

            // solo si aún no tiene agente
            if (ticket.getAgente() == null) {
                ticket.setAgente(usuario);
            }
        }

        ticket.setEstado(estadoNuevo);

        Ticket saved = ticketRepository.save(ticket);

        registrarBitacora(
                saved,
                estadoAnterior,
                estadoNuevo,
                usuario,
                "Cambio de estado: "
                        + (estadoAnterior != null
                        ? estadoAnterior.getNombre()
                        : "—")
                        + " → "
                        + estadoNuevo.getNombre()
        );

        return saved;
    }

    @Transactional
    public Ticket asignar(Long ticketId, Long agenteId, UserDetails userDetails) {
        Ticket ticket = findById(ticketId);
        Estado estadoAnterior = ticket.getEstado();

        Usuario agente = usuarioRepository.findById(agenteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Agente no encontrado: " + agenteId));

        Usuario solicitante = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow();

        ticket.setAgente(agente);

        Estado enProceso = estadoRepository.findByNombre("En Proceso").orElse(null);
        if (enProceso != null) ticket.setEstado(enProceso);

        Ticket saved = ticketRepository.save(ticket);

        registrarBitacora(saved, estadoAnterior, enProceso, solicitante,
                "Asignado a: " + agente.getNombre());

        return saved;
    }

    // ── ELIMINAR
    @Transactional
    public void delete(Long id) {
        ticketRepository.delete(findById(id));
    }

    private void registrarBitacora(Ticket ticket, Estado anterior,
                                   Estado nuevo, Usuario usuario,
                                   String comentario) {
        Bitacora b = new Bitacora();
        b.setTicket(ticket);
        b.setUsuario(usuario);
        b.setEstadoAnterior(anterior);
        b.setEstadoNuevo(nuevo);
        b.setComentario(comentario);
        bitacoraRepository.save(b);
    }

    @Transactional(readOnly = true)
    public Page<Ticket> search(String query, Pageable pageable) {
        return ticketRepository.search(query, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Ticket> findByAgente(Long agenteId, Pageable pageable) {
        Usuario agente = usuarioRepository.findById(agenteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Agente no encontrado"));
        return ticketRepository.findByAgente(agente, pageable);
    }

    public Usuario getUsuarioByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Transactional(readOnly = true)
    public Page<Ticket> getByCliente(
            Long clienteId,
            String estado,
            String prioridad,
            Pageable pageable) {

        return ticketRepository.findByClienteUsuarioId(
                clienteId,
                pageable
        );
    }
}