package com.helpdesk.helpdesk_pro.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.helpdesk.helpdesk_pro.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_USER;

    private boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    @JsonIgnore
    public String getUsername()             { return email; }
    @Override
    @JsonIgnore
    public boolean isAccountNonExpired()    { return true; }
    @Override
    @JsonIgnore
    public boolean isAccountNonLocked()     { return active; }
    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired(){ return true; }
    @Override
    @JsonIgnore
    public boolean isEnabled()              { return active; }
}