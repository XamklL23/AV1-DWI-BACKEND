package com.helpdesk.helpdesk_pro;

import com.helpdesk.helpdesk_pro.entity.Ticket;
import com.helpdesk.helpdesk_pro.enums.TicketPriority;
import com.helpdesk.helpdesk_pro.enums.TicketStatus;
import com.helpdesk.helpdesk_pro.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void whenSaveTicket_thenFindById() {
        Ticket t = new Ticket();
        t.setTitle("Error en nómina");
        t.setStatus(TicketStatus.NUEVO);
        t.setPriority(TicketPriority.CRITICA);

        Ticket saved = ticketRepository.save(t);

        assertNotNull(saved.getId());
        assertEquals("Error en nómina", saved.getTitle());
        assertEquals(TicketStatus.NUEVO, saved.getStatus());
    }

    @Test
    void whenFindByStatus_thenReturnPage() {
        Ticket t = new Ticket();
        t.setTitle("Test");
        t.setStatus(TicketStatus.NUEVO);
        ticketRepository.save(t);

        Page<Ticket> result = ticketRepository
                .findByStatus(TicketStatus.NUEVO, PageRequest.of(0, 10));

        assertNotNull(result);
        assertTrue(result.getTotalElements() > 0);
    }

    @Test
    void whenCountByStatus_thenReturnCorrectCount() {
        long before = ticketRepository.countByStatus(TicketStatus.RESUELTO);
        Ticket t = new Ticket();
        t.setStatus(TicketStatus.RESUELTO);
        ticketRepository.save(t);

        assertEquals(before + 1,
                ticketRepository.countByStatus(TicketStatus.RESUELTO));
    }
}