package com.helpdesk.helpdesk_pro;

import com.helpdesk.helpdesk_pro.dto.request.TicketCreateRequest;
import com.helpdesk.helpdesk_pro.entity.*;
import com.helpdesk.helpdesk_pro.enums.Role;
import com.helpdesk.helpdesk_pro.repository.*;
import com.helpdesk.helpdesk_pro.service.TicketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private EstadoRepository estadoRepository;
    @Mock private PrioridadRepository prioridadRepository;
    @InjectMocks private TicketService ticketService;

    // ─────────────────────────────────────────────
    @Test
    void whenCreateTicket_thenReturnSaved() {

        Usuario cliente = new Usuario();
        cliente.setEmail("cliente@test.com");
        cliente.setNombre("Cliente Test");
        cliente.setRol(Role.CLIENTE);

        Estado estado = new Estado();
        estado.setEstadoId(1L);
        estado.setNombre("Abierto");

        Prioridad prioridad = new Prioridad();
        prioridad.setPrioridadId(2L);
        prioridad.setNombre("Media");

        TicketCreateRequest req = new TicketCreateRequest();
        req.setTitulo("Impresora no imprime");
        req.setDescripcionInicial("No responde al enviar trabajos");
        req.setPrioridadId(2L);

        Ticket savedTicket = new Ticket();
        savedTicket.setTitulo("Impresora no imprime");
        savedTicket.setEstado(estado);
        savedTicket.setPrioridad(prioridad);
        savedTicket.setCliente(cliente);

        UserDetails ud = mock(UserDetails.class);
        when(ud.getUsername()).thenReturn("cliente@test.com");

        when(usuarioRepository.findByEmail("cliente@test.com"))
                .thenReturn(Optional.of(cliente));
        when(estadoRepository.findById(1L))
                .thenReturn(Optional.of(estado));
        when(prioridadRepository.findById(2L))
                .thenReturn(Optional.of(prioridad));
        when(ticketRepository.save(any()))
                .thenReturn(savedTicket);

        Ticket result = ticketService.create(req, ud);

        assertEquals("Impresora no imprime", result.getTitulo());
        assertEquals("Abierto", result.getEstado().getNombre());
        verify(ticketRepository, times(1)).save(any());
    }

    // ─────────────────────────────────────────────
    @Test
    void whenUpdateEstado_thenEstadoCambia() {

        UserDetails ud = mock(UserDetails.class);
        when(ud.getUsername()).thenReturn("cliente@test.com");

        Ticket ticket = new Ticket();
        ticket.setTicketId(1L);

        Estado nuevoEstado = new Estado();
        nuevoEstado.setEstadoId(2L);
        nuevoEstado.setNombre("En Proceso");

        Usuario usuario = new Usuario();
        usuario.setEmail("cliente@test.com");

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));
        when(estadoRepository.findById(2L))
                .thenReturn(Optional.of(nuevoEstado));
        when(usuarioRepository.findByEmail("cliente@test.com"))
                .thenReturn(Optional.of(usuario));
        when(ticketRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        Ticket result = ticketService.updateEstado(1L, 2L, ud);

        assertEquals("En Proceso", result.getEstado().getNombre());
    }

    // ─────────────────────────────────────────────
    @Test
    void whenAsignarAgente_thenAgenteAsignado() {

        UserDetails ud = mock(UserDetails.class);
        when(ud.getUsername()).thenReturn("admin@test.com");

        Ticket ticket = new Ticket();
        ticket.setTicketId(1L);

        Usuario agente = new Usuario();
        agente.setUsuarioId(5L);
        agente.setNombre("Agente Test");

        Estado enProceso = new Estado();
        enProceso.setNombre("En Proceso");

        Usuario usuario = new Usuario();
        usuario.setEmail("admin@test.com");

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));
        when(usuarioRepository.findById(5L))
                .thenReturn(Optional.of(agente));
        when(usuarioRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(usuario));
        when(estadoRepository.findByNombre("En Proceso"))
                .thenReturn(Optional.of(enProceso));
        when(ticketRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        Ticket result = ticketService.asignar(1L, 5L, ud);

        assertEquals("Agente Test", result.getAgente().getNombre());
        assertEquals("En Proceso", result.getEstado().getNombre());
    }

    // ─────────────────────────────────────────────
    @Test
    void whenTicketNotFound_thenThrowException() {

        when(ticketRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> ticketService.findById(99L));
    }
}