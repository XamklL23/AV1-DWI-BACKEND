package com.helpdesk.helpdesk_pro.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketCreateRequest {
    @NotBlank(message = "El título es requerido")
    private String titulo;

    @NotBlank(message = "La descripción es requerida")
    private String descripcionInicial;

    @NotNull(message = "La prioridad es requerida")
    private Long prioridadId;
}