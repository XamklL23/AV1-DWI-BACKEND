package com.helpdesk.helpdesk_pro.dto.response;

import com.helpdesk.helpdesk_pro.entity.Ticket;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class TicketResponse {
    private Long           ticketId;
    private String         titulo;
    private String         descripcionInicial;
    private String         estadoNombre;
    private String         prioridadNombre;
    private String         clienteNombre;
    private String         agenteNombre;
    private OffsetDateTime fechaCreacion;
    private OffsetDateTime fechaActualizacion;

    public static TicketResponse from(Ticket t) {
        TicketResponse r = new TicketResponse();
        r.setTicketId(t.getTicketId());
        r.setTitulo(t.getTitulo());
        r.setDescripcionInicial(t.getDescripcionInicial());
        r.setFechaCreacion(t.getFechaCreacion());
        r.setFechaActualizacion(t.getFechaActualizacion());
        if (t.getEstado()    != null) r.setEstadoNombre(t.getEstado().getNombre());
        if (t.getPrioridad() != null) r.setPrioridadNombre(t.getPrioridad().getNombre());
        if (t.getCliente()   != null) r.setClienteNombre(t.getCliente().getNombre());
        if (t.getAgente()    != null) r.setAgenteNombre(t.getAgente().getNombre());
        return r;
    }
}