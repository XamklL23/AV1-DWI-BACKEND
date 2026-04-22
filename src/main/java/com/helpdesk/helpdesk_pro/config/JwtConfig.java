package com.helpdesk.helpdesk_pro.config;

import com.helpdesk.helpdesk_pro.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

// config/JwtConfig.java
@Configuration
public class JwtConfig {
    // Los valores se inyectan desde application.properties con @Value en JwtUtil.
    // Esta clase existe para centralizar beans adicionales de JWT si se necesitan.

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return email -> userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado: " + email));
    }
}