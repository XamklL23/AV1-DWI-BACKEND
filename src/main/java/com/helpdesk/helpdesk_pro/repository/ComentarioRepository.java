package com.helpdesk.helpdesk_pro.repository;

import com.helpdesk.helpdesk_pro.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByTicketTicketIdOrderByFechaCreacionAsc(Long ticketId);
}