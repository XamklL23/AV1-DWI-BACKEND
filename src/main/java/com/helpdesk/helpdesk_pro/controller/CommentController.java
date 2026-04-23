package com.helpdesk.helpdesk_pro.controller;

import com.helpdesk.helpdesk_pro.entity.Comentario;
import com.helpdesk.helpdesk_pro.entity.Ticket;
import com.helpdesk.helpdesk_pro.entity.Usuario;
import com.helpdesk.helpdesk_pro.repository.ComentarioRepository;
import com.helpdesk.helpdesk_pro.repository.UsuarioRepository;
import com.helpdesk.helpdesk_pro.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final ComentarioRepository comentarioRepository;
    private final TicketService        ticketService;
    private final UsuarioRepository    usuarioRepository;

    @GetMapping
    public ResponseEntity<List<Comentario>> getByTicket(
            @PathVariable Long ticketId) {
        return ResponseEntity.ok(
                // ← nombre correcto del método en ComentarioRepository
                comentarioRepository.findByTicketTicketIdOrderByFechaCreacionAsc(ticketId));
    }

    @PostMapping
    public ResponseEntity<Comentario> create(
            @PathVariable Long ticketId,
            @RequestBody Comentario comentario,
            @AuthenticationPrincipal UserDetails userDetails) {

        Ticket ticket = ticketService.findById(ticketId);
        Usuario autor = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow();
        comentario.setTicket(ticket);
        comentario.setAutor(autor);  // ← setAutor() no setAuthor()
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(comentarioRepository.save(comentario));
    }

    @DeleteMapping("/{comentarioId}")
    public ResponseEntity<Void> delete(@PathVariable Long comentarioId) {
        comentarioRepository.deleteById(comentarioId);
        return ResponseEntity.noContent().build();
    }
}