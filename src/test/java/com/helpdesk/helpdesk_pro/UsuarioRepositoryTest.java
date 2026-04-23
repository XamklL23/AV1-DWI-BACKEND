package com.helpdesk.helpdesk_pro;

import com.helpdesk.helpdesk_pro.entity.Usuario;
import com.helpdesk.helpdesk_pro.enums.Role;
import com.helpdesk.helpdesk_pro.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired private UsuarioRepository usuarioRepository;

    private Usuario crearUsuario(String nombre, String email, Role rol) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPassword("hash123");
        u.setRol(rol);
        return usuarioRepository.save(u);
    }

    @Test
    void whenSaveUsuario_thenFindById() {
        Usuario saved = crearUsuario("Juan Perez", "juan@test.com", Role.cliente);

        assertNotNull(saved.getUsuarioId());
        assertEquals("Juan Perez", saved.getNombre());
        assertEquals(Role.cliente, saved.getRol());
    }

    @Test
    void whenFindByEmail_thenReturnUsuario() {
        crearUsuario("Ana Lopez", "ana@test.com", Role.agente);

        Optional<Usuario> result = usuarioRepository.findByEmail("ana@test.com");

        assertTrue(result.isPresent());
        assertEquals("Ana Lopez", result.get().getNombre());
    }

    @Test
    void whenEmailNotExists_thenReturnEmpty() {
        Optional<Usuario> result = usuarioRepository.findByEmail("noexiste@test.com");
        assertFalse(result.isPresent());
    }

    @Test
    void whenExistsByEmail_thenReturnTrue() {
        crearUsuario("Carlos", "carlos@test.com", Role.cliente);
        assertTrue(usuarioRepository.existsByEmail("carlos@test.com"));
    }

    @Test
    void whenFindByRol_thenReturnList() {
        crearUsuario("Agente1", "agente1@test.com", Role.agente);
        crearUsuario("Agente2", "agente2@test.com", Role.agente);
        crearUsuario("Cliente1", "cliente1@test.com", Role.cliente);

        List<Usuario> agentes = usuarioRepository.findByRol(Role.agente);

        assertEquals(2, agentes.size());
    }
}