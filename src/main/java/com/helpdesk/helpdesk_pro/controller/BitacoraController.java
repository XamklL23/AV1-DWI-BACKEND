package com.helpdesk.helpdesk_pro.controller;

import com.helpdesk.helpdesk_pro.entity.Bitacora;
import com.helpdesk.helpdesk_pro.repository.BitacoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/bitacora")
@RequiredArgsConstructor
public class BitacoraController {

    private final BitacoraRepository bitacoraRepository;

    @GetMapping
    public ResponseEntity<List<Bitacora>> getBitacora(@PathVariable Long ticketId) {
        return ResponseEntity.ok(
                bitacoraRepository.findByTicketTicketIdOrderByFechaCambioAsc(ticketId));
    }
}