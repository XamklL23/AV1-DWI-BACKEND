package com.helpdesk.helpdesk_pro.controller;

import com.helpdesk.helpdesk_pro.entity.Ticket;
import com.helpdesk.helpdesk_pro.entity.User;
import com.helpdesk.helpdesk_pro.enums.TicketPriority;
import com.helpdesk.helpdesk_pro.enums.TicketStatus;
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

// TicketController.java
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    // ── GET ALL (paginado + filtros) ─────────────────────────
    @GetMapping
    public ResponseEntity<Page<Ticket>> getAll(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,
            Pageable pageable) {
        return ResponseEntity.ok(ticketService.getAll(status, priority, pageable));
    }

    // ── GET BY ID ────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.findById(id));
    }

    // ── POST (crear) ─────────────────────────────────────────
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Ticket> create(
            @Valid @RequestBody Ticket ticket,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(201)
                .body(ticketService.create(ticket, userDetails));
    }

    // ── PUT (actualizar completo) ─────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<Ticket> update(
            @PathVariable Long id,
            @Valid @RequestBody Ticket ticket) {
        return ResponseEntity.ok(ticketService.update(id, ticket));
    }

    // ── PATCH (cambiar estado) ────────────────────────────────
    @PatchMapping("/{id}/status")
    public ResponseEntity<Ticket> updateStatus(
            @PathVariable Long id,
            @RequestParam TicketStatus status) {
        return ResponseEntity.ok(ticketService.updateStatus(id, status));
    }

    // ── PATCH (asignar agente) ────────────────────────────────
    @PatchMapping("/{id}/assign/{agentId}")
    public ResponseEntity<Ticket> assign(
            @PathVariable Long id,
            @PathVariable Long agentId) {
        return ResponseEntity.ok(ticketService.assign(id, agentId));
    }

    // ── DELETE ───────────────────────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }
}