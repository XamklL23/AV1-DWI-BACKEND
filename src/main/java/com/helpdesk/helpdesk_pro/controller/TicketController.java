package com.helpdesk.helpdesk_pro.controller;

import com.helpdesk.helpdesk_pro.dto.request.TicketCreateRequest;
import com.helpdesk.helpdesk_pro.entity.Ticket;
import com.helpdesk.helpdesk_pro.repository.TicketRepository;
import com.helpdesk.helpdesk_pro.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    // ── GET ALL ──────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<Page<Ticket>> getAll(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String prioridad,
            @RequestParam(required = false) String search,
            Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails) {

        Page<Ticket> tickets;

        // Si es búsqueda
        if (search != null && !search.isBlank()) {
            tickets = ticketService.search(search, pageable);

        } else {

            // Obtener usuario autenticado
            var usuario = ticketService.getUsuarioByEmail(userDetails.getUsername());

            // CLIENTE → solo sus tickets
            if (usuario.getRol().name().equals("CLIENTE")) {

                tickets = ticketService.getByCliente(
                        usuario.getUsuarioId(),
                        estado,
                        prioridad,
                        pageable
                );

            } else {
                // ADMIN / AGENTE → todos
                tickets = ticketService.getAll(
                        estado,
                        prioridad,
                        pageable
                );
            }
        }

        return ResponseEntity.ok(tickets);
    }

    // ── GET BY ID ────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.findById(id));
    }

    // ── CREATE ───────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Ticket> create(
            @Valid @RequestBody TicketCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketService.create(request, userDetails));
    }

    // ── UPDATE ───────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<Ticket> update(
            @PathVariable Long id,
            @Valid @RequestBody TicketCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ticketService.update(id, request, userDetails));
    }

    // ── CAMBIO DE ESTADO ──────────────────────
    @PatchMapping("/{id}/estado/{estadoId}")
    public ResponseEntity<Ticket> updateEstado(
            @PathVariable Long id,
            @PathVariable Long estadoId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ticketService.updateEstado(id, estadoId, userDetails));
    }

    // ── ASIGNAR AGENTE (CON BITÁCORA) ────────────────────────
    @PatchMapping("/{id}/asignar/{agenteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENTE')")
    public ResponseEntity<Ticket> asignar(
            @PathVariable Long id,
            @PathVariable Long agenteId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ticketService.asignar(id, agenteId, userDetails));
    }

    // ── DELETE (solo ADMIN) ───────────────────────────────────
    // ── DELETE (solo ADMIN) ───────────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ── GET TICKETS POR AGENTE ────────────────────────────────
    @GetMapping("/agente/{agenteId}")
    @PreAuthorize("hasAnyRole('admin', 'agente')")
    public ResponseEntity<Page<Ticket>> getByAgente(
            @PathVariable Long agenteId,
            Pageable pageable) {
        return ResponseEntity.ok(ticketService.findByAgente(agenteId, pageable));
    }
}