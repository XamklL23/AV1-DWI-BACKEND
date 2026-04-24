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

    private final TicketRepository ticketRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstadoRepository estadoRepository;
    private final PrioridadRepository prioridadRepository;
    private final BitacoraRepository bitacoraRepository;

    public Page<Ticket> getAll(String estado, String prioridad, Pageable pageable) {
        if (estado != null && !estado.isBlank()) {
            return ticketRepository.findByEstadoNombre(estado, pageable);
        }
        if (prioridad != null && !prioridad.isBlank()) {
            return ticketRepository.findByPrioridadNombre(prioridad, pageable);
        }
        return ticketRepository.findAll(pageable);
    }

    public Page<Ticket> search(String search, Pageable pageable) {
        return ticketRepository
                .findByTituloContainingIgnoreCaseOrDescripcionInicialContainingIgnoreCaseOrCliente_NombreContainingIgnoreCase(
                        search,
                        search,
                        search,
                        pageable
                );
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

        Ticket saved = ticketRepository.save(ticket);

        registrarBitacora(saved, null, estadoInicial, cliente, "Ticket creado");

        return saved;
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

    public Ticket updateEstado(Long ticketId, Long estadoId, UserDetails userDetails) {

        Ticket ticket = findById(ticketId);
        Estado anterior = ticket.getEstado();

        Estado nuevo = estadoRepository.findById(estadoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estado no encontrado: " + estadoId));

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        ticket.setEstado(nuevo);
        Ticket saved = ticketRepository.save(ticket);

        registrarBitacora(saved, anterior, nuevo, usuario, "Cambio de estado");

        return saved;
    }

    public Ticket asignar(Long ticketId, Long agenteId, UserDetails userDetails) {

        Ticket ticket = findById(ticketId);
        Estado anterior = ticket.getEstado();

        Usuario agente = usuarioRepository.findById(agenteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Agente no encontrado"));

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        ticket.setAgente(agente);

        Estado enProceso = estadoRepository.findByNombre("En Proceso")
                .orElse(null);

        if (enProceso != null) {
            ticket.setEstado(enProceso);
        }

        Ticket saved = ticketRepository.save(ticket);

        registrarBitacora(saved, anterior, enProceso, usuario,
                "Asignado a agente: " + agente.getNombre());

        return saved;
    }

    public void delete(Long id) {
        ticketRepository.delete(findById(id));
    }

    private void registrarBitacora(Ticket ticket,
                                   Estado anterior,
                                   Estado nuevo,
                                   Usuario usuario,
                                   String comentario) {

        Bitacora bitacora = new Bitacora();
        bitacora.setTicket(ticket);
        bitacora.setEstadoAnterior(anterior);
        bitacora.setEstadoNuevo(nuevo);
        bitacora.setUsuario(usuario);
        bitacora.setComentario(comentario);

        bitacoraRepository.save(bitacora);
    }
}