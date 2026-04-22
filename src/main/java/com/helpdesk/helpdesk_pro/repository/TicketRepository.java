package com.helpdesk.helpdesk_pro.repository;

import com.helpdesk.helpdesk_pro.entity.Ticket;
import com.helpdesk.helpdesk_pro.entity.User;
import com.helpdesk.helpdesk_pro.enums.TicketPriority;
import com.helpdesk.helpdesk_pro.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// TicketRepository.java
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);
    Page<Ticket> findByAssignedTo(User user, Pageable pageable);
    Page<Ticket> findByPriority(TicketPriority priority, Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.title LIKE %:q% OR t.description LIKE %:q%")
    Page<Ticket> search(@Param("q") String query, Pageable pageable);

    long countByStatus(TicketStatus status);
    Optional<Ticket> findByTicketNumber(String ticketNumber);
}