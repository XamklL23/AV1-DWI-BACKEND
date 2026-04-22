package com.helpdesk.helpdesk_pro.service;

import com.helpdesk.helpdesk_pro.entity.User;
import com.helpdesk.helpdesk_pro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado: " + id));
    }

    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El email ya está registrado");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User update(Long id, User data) {
        User existing = findById(id);
        existing.setFullName(data.getFullName());
        if (data.getPassword() != null && !data.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(data.getPassword()));
        }
        return userRepository.save(existing);
    }

    public void deactivate(Long id) {
        User user = findById(id);
        user.setActive(false);
        userRepository.save(user);
    }
}