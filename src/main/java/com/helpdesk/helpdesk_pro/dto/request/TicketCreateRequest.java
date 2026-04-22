package com.helpdesk.helpdesk_pro.dto.request;

import com.helpdesk.helpdesk_pro.enums.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class TicketCreateRequest {

    @NotBlank(message = "El título es requerido")
    private String title;

    @NotBlank(message = "La descripción es requerida")
    private String description;

    @NotNull(message = "La prioridad es requerida")
    private TicketPriority priority;

    private Long categoryId;

    private List<String> tags;
}