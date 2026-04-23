package com.helpdesk.helpdesk_pro.controller;

import com.helpdesk.helpdesk_pro.entity.Usuario;
import com.helpdesk.helpdesk_pro.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UsuarioService usuarioService; // ← UserService → UsuarioService

    @GetMapping
    @PreAuthorize("hasRole('admin')") // ← 'ADMIN' → 'admin'
    public ResponseEntity<List<Usuario>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> findById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> update(@PathVariable Long id,
                                          @RequestBody Usuario data) {
        return ResponseEntity.ok(usuarioService.update(id, data));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')") // ← 'ADMIN' → 'admin'
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}