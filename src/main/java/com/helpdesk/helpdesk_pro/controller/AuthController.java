package com.helpdesk.helpdesk_pro.controller;

import com.helpdesk.helpdesk_pro.dto.request.LoginRequest;
import com.helpdesk.helpdesk_pro.dto.response.JwtResponse;
import com.helpdesk.helpdesk_pro.entity.User;
import com.helpdesk.helpdesk_pro.security.JwtUtil;
import com.helpdesk.helpdesk_pro.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        User user = (User) auth.getPrincipal();
        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(new JwtResponse(token, user));
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(
            @Valid @RequestBody User user) {

        User saved = userService.register(user);
        String token = jwtUtil.generateToken(saved);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new JwtResponse(token, saved));
    }
}