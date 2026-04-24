package com.helpdesk.helpdesk_pro.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil            jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        try {
            String header = request.getHeader("Authorization");

            System.out.println("🔐 HEADER: " + header);

            if (header == null || !header.startsWith("Bearer ")) {
                System.out.println("⛔ No hay token o formato incorrecto");
                chain.doFilter(request, response);
                return;
            }

            String token = header.substring(7).trim();
            System.out.println("🧾 TOKEN: " + token);

            String username = jwtUtil.extractUsername(token);
            System.out.println("👤 USERNAME EXTRAÍDO: " + username);

            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails user = userDetailsService.loadUserByUsername(username);
                System.out.println("USER DETAILS: " + user.getUsername());

                boolean valido = jwtUtil.isValid(token, user);
                System.out.println("TOKEN VÁLIDO?: " + valido);

                if (valido) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    user, null, user.getAuthorities());

                    auth.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(auth);

                    System.out.println("USUARIO AUTENTICADO CORRECTAMENTE");
                } else {
                    System.out.println("TOKEN INVÁLIDO");
                }
            }

        } catch (Exception e) {
            System.err.println("ERROR EN JWT FILTER: " + e.getMessage());
        }

        chain.doFilter(request, response);
    }

}