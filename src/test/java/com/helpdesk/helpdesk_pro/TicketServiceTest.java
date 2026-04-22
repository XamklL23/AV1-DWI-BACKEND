package com.helpdesk.helpdesk_pro;

import com.helpdesk.helpdesk_pro.entity.Ticket;
import com.helpdesk.helpdesk_pro.entity.User;
import com.helpdesk.helpdesk_pro.enums.TicketStatus;
import com.helpdesk.helpdesk_pro.repository.TicketRepository;
import com.helpdesk.helpdesk_pro.repository.UserRepository;
import com.helpdesk.helpdesk_pro.service.TicketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock    private UserRepository userRepository;
    @InjectMocks
    private TicketService ticketService;

    @Test
    void whenCreateTicket_thenReturnSaved() {
        User user = new User();
        user.setEmail("test@test.com");

        Ticket ticket = new Ticket();
        ticket.setTitle("Impresora no imprime");

        UserDetails ud = mock(UserDetails.class);
        when(ud.getUsername()).thenReturn("test@test.com");
        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));
        when(ticketRepository.save(any())).thenReturn(ticket);

        Ticket result = ticketService.create(ticket, ud);

        assertEquals("Impresora no imprime", result.getTitle());
        verify(ticketRepository, times(1)).save(any());
    }

    @Test
    void whenUpdateStatus_thenStatusChanged() {
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setStatus(TicketStatus.NUEVO);

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Ticket result = ticketService.updateStatus(1L, TicketStatus.EN_PROGRESO);

        assertEquals(TicketStatus.EN_PROGRESO, result.getStatus());
    }

    @Test
    void whenTicketNotFound_thenThrowException() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> ticketService.findById(99L));
    }
}