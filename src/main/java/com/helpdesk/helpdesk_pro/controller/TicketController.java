package com.helpdesk.helpdesk_pro.controller;

import com.helpdesk.helpdesk_pro.dto.request.TicketCreateRequest;
import com.helpdesk.helpdesk_pro.entity.Ticket;
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

    // ── GET ALL (paginado + filtros) ─────────────────────────
    @GetMapping
    public ResponseEntity<Page<Ticket>> getAll(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String prioridad,
            Pageable pageable) {
        return ResponseEntity.ok(ticketService.getAll(estado, prioridad, pageable));
    }

    // ── GET BY ID ────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.findById(id));
    }

    // ── POST (crear) ─────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Ticket> create(
            @Valid @RequestBody TicketCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketService.create(request, userDetails));

    }

    // ── PUT (actualizar completo) ─────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<Ticket> update(
            @PathVariable Long id,
            @Valid @RequestBody TicketCreateRequest request) {
        return ResponseEntity.ok(ticketService.update(id, request));
    }

    // ── PATCH (cambiar estado) ────────────────────────────────
    @PatchMapping("/{id}/estado/{estadoId}")
    public ResponseEntity<Ticket> updateEstado(
            @PathVariable Long id,
            @PathVariable Long estadoId) {
        return ResponseEntity.ok(ticketService.updateEstado(id, estadoId));
    }

    // ── PATCH (asignar agente) ────────────────────────────────
    @PatchMapping("/{id}/asignar/{agenteId}")
    public ResponseEntity<Ticket> asignar(
            @PathVariable Long id,
            @PathVariable Long agenteId) {
        return ResponseEntity.ok(ticketService.asignar(id, agenteId));
    }

    // ── DELETE ───────────────────────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }

}