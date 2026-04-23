package com.helpdesk.helpdesk_pro.dto.response;

import com.helpdesk.helpdesk_pro.entity.Usuario;
import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long   id;
    private String email;
    private String nombre;
    private String rol;

    public JwtResponse(String token, Usuario usuario) {
        this.token  = token;
        this.id     = usuario.getUsuarioId();
        this.email  = usuario.getEmail();
        this.nombre = usuario.getNombre();
        this.rol    = usuario.getRol().name();
    }
}