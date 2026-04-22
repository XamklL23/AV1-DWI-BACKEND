package com.helpdesk.helpdesk_pro.controller;

import com.helpdesk.helpdesk_pro.entity.Comment;
import com.helpdesk.helpdesk_pro.entity.Ticket;
import com.helpdesk.helpdesk_pro.entity.User;
import com.helpdesk.helpdesk_pro.repository.CommentRepository;
import com.helpdesk.helpdesk_pro.repository.UserRepository;
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

    private final CommentRepository commentRepository;
    private final TicketService ticketService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Comment>> getByTicket(
            @PathVariable Long ticketId) {
        return ResponseEntity.ok(
                commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId));
    }

    @PostMapping
    public ResponseEntity<Comment> create(
            @PathVariable Long ticketId,
            @RequestBody Comment comment,
            @AuthenticationPrincipal UserDetails userDetails) {

        Ticket ticket = ticketService.findById(ticketId);
        User author = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow();
        comment.setTicket(ticket);
        comment.setAuthor(author);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentRepository.save(comment));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId) {
        commentRepository.deleteById(commentId);
        return ResponseEntity.noContent().build();
    }
}