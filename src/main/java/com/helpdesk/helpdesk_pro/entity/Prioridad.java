package com.helpdesk.helpdesk_pro.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prioridades")
@Data
@NoArgsConstructor
public class Prioridad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prioridad_id")
    private Long prioridadId;

    @Column(unique = true, nullable = false, length = 20)
    private String nombre;
}