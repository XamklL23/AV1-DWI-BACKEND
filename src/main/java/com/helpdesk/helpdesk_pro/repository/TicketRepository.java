package com.helpdesk.helpdesk_pro.repository;

import com.helpdesk.helpdesk_pro.entity.Ticket;
import com.helpdesk.helpdesk_pro.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findByCliente(Usuario cliente, Pageable pageable);

    Page<Ticket> findByAgente(Usuario agente, Pageable pageable);

    Page<Ticket> findByEstadoNombre(String estadoNombre, Pageable pageable);

    Page<Ticket> findByPrioridadNombre(String prioridadNombre, Pageable pageable);

    long countByEstadoNombre(String estadoNombre);

    Page<Ticket> findByTituloContainingIgnoreCaseOrDescripcionInicialContainingIgnoreCaseOrCliente_NombreContainingIgnoreCase(
            String titulo,
            String descripcion,
            String cliente,
            Pageable pageable
    );

    @Query("SELECT t FROM Ticket t WHERE " +
            "LOWER(t.titulo) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(t.descripcionInicial) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Ticket> search(@Param("q") String query, Pageable pageable);

    Page<Ticket> findByClienteUsuarioId(
            Long clienteId,
            Pageable pageable
    );
}
