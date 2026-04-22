package com.helpdesk.helpdesk_pro.dto.response;

import com.helpdesk.helpdesk_pro.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long   id;
    private String email;
    private String fullName;
    private String role;

    public JwtResponse(String token, User user) {
        this.token    = token;
        this.id       = user.getId();
        this.email    = user.getEmail();
        this.fullName = user.getFullName();
        this.role     = user.getRole().name();
    }
}