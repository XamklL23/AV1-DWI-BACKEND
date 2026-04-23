package com.helpdesk.helpdesk_pro.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(name = "descripcion_inicial", nullable = false, columnDefinition = "TEXT")
    private String descripcionInicial;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Usuario cliente;

    @ManyToOne
    @JoinColumn(name = "agente_id")
    private Usuario agente;

    @ManyToOne
    @JoinColumn(name = "estado_id")
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "prioridad_id")
    private Prioridad prioridad;

    @CreationTimestamp
    @Column(name = "fecha_creacion")
    private OffsetDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private OffsetDateTime fechaActualizacion;

    @JsonIgnore  // ← evita referencia circular Ticket → Comentario → Ticket
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<Comentario> comentarios;
}