package com.helpdesk.helpdesk_pro.controller;

import com.helpdesk.helpdesk_pro.dto.request.LoginRequest;
import com.helpdesk.helpdesk_pro.dto.request.RegisterRequest;
import com.helpdesk.helpdesk_pro.dto.response.JwtResponse;
import com.helpdesk.helpdesk_pro.entity.Usuario;
import com.helpdesk.helpdesk_pro.service.UsuarioService;
import com.helpdesk.helpdesk_pro.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    // ===== LOGIN =====
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Usuario usuario = (Usuario) auth.getPrincipal();
        String token = jwtUtil.generateToken(usuario);

        return ResponseEntity.ok(new JwtResponse(token, usuario));
    }

    // ===== REGISTER =====
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request) {

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(request.getPassword());


        Usuario saved = usuarioService.register(usuario);
        String token = jwtUtil.generateToken(saved);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new JwtResponse(token, saved));
    }
}