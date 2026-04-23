package com.helpdesk.helpdesk_pro.repository;

import com.helpdesk.helpdesk_pro.entity.Usuario;
import com.helpdesk.helpdesk_pro.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Usuario> findByRol(Role rol);
}