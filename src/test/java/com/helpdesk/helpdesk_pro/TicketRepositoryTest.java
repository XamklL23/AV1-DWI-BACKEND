package com.helpdesk.helpdesk_pro;

import com.helpdesk.helpdesk_pro.entity.*;
import com.helpdesk.helpdesk_pro.enums.Role;
import com.helpdesk.helpdesk_pro.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TicketRepositoryTest {

    @Autowired private TicketRepository ticketRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EstadoRepository estadoRepository;
    @Autowired private PrioridadRepository prioridadRepository;

    private Estado estadoAbierto;
    private Prioridad prioridadMedia;
    private Usuario cliente;

    @BeforeEach
    void setUp() {
        estadoAbierto = new Estado();
        estadoAbierto.setNombre("Abierto");
        estadoAbierto = estadoRepository.save(estadoAbierto);

        prioridadMedia = new Prioridad();
        prioridadMedia.setNombre("Media");
        prioridadMedia = prioridadRepository.save(prioridadMedia);

        cliente = new Usuario();
        cliente.setNombre("Cliente Test");
        cliente.setEmail("cliente@test.com");
        cliente.setPassword("hash123");
        cliente.setRol(Role.CLIENTE);
        cliente = usuarioRepository.save(cliente);
    }

    @Test
    void whenSaveTicket_thenFindById() {
        Ticket ticket = new Ticket();
        ticket.setTitulo("Error en nómina");
        ticket.setDescripcionInicial("No puedo acceder al sistema");
        ticket.setCliente(cliente);
        ticket.setEstado(estadoAbierto);
        ticket.setPrioridad(prioridadMedia);

        Ticket saved = ticketRepository.save(ticket);

        assertNotNull(saved.getTicketId());
        assertEquals("Error en nómina", saved.getTitulo());
        assertEquals("Abierto", saved.getEstado().getNombre());
    }

    @Test
    void whenFindByEstadoNombre_thenReturnPage() {
        Ticket ticket = new Ticket();
        ticket.setTitulo("Ticket de prueba");
        ticket.setDescripcionInicial("Descripción de prueba");
        ticket.setCliente(cliente);
        ticket.setEstado(estadoAbierto);
        ticket.setPrioridad(prioridadMedia);
        ticketRepository.save(ticket);

        Page<Ticket> result =
                ticketRepository.findByEstadoNombre("Abierto", PageRequest.of(0, 10));

        assertNotNull(result);
        assertTrue(result.getTotalElements() > 0);
        assertEquals("Abierto", result.getContent().get(0).getEstado().getNombre());
    }

    @Test
    void whenFindByCliente_thenReturnPage() {
        Ticket ticket = new Ticket();
        ticket.setTitulo("Ticket del cliente");
        ticket.setDescripcionInicial("Descripción");
        ticket.setCliente(cliente);
        ticket.setEstado(estadoAbierto);
        ticket.setPrioridad(prioridadMedia);
        ticketRepository.save(ticket);

        Page<Ticket> result =
                ticketRepository.findByCliente(cliente, PageRequest.of(0, 10));

        assertNotNull(result);
        assertTrue(result.getTotalElements() > 0);
    }

    @Test
    void whenCountByEstadoNombre_thenReturnCorrectCount() {
        Ticket ticket = new Ticket();
        ticket.setTitulo("Ticket conteo");
        ticket.setDescripcionInicial("Descripción");
        ticket.setCliente(cliente);
        ticket.setEstado(estadoAbierto);
        ticket.setPrioridad(prioridadMedia);
        ticketRepository.save(ticket);

        long count = ticketRepository.countByEstadoNombre("Abierto");
        assertTrue(count > 0);
    }

    @Test
    void whenSearch_thenReturnMatchingTickets() {
        Ticket ticket = new Ticket();
        ticket.setTitulo("Impresora no funciona");
        ticket.setDescripcionInicial("La impresora del 3er piso no responde");
        ticket.setCliente(cliente);
        ticket.setEstado(estadoAbierto);
        ticket.setPrioridad(prioridadMedia);
        ticketRepository.save(ticket);

        Page<Ticket> result =
                ticketRepository
                        .findByTituloContainingIgnoreCaseOrDescripcionInicialContainingIgnoreCaseOrCliente_NombreContainingIgnoreCase(
                                "impresora",
                                "impresora",
                                "impresora",
                                PageRequest.of(0, 10)
                        );

        assertNotNull(result);
        assertTrue(result.getTotalElements() > 0);
    }
}