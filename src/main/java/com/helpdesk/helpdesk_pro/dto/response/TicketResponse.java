package com.helpdesk.helpdesk_pro.dto.response;


import com.helpdesk.helpdesk_pro.entity.Ticket;
import com.helpdesk.helpdesk_pro.enums.TicketPriority;
import com.helpdesk.helpdesk_pro.enums.TicketStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class TicketResponse {
    private Long          id;
    private String        ticketNumber;
    private String        title;
    private String        description;
    private TicketStatus status;
    private TicketPriority priority;
    private String        createdByName;
    private String        assignedToName;
    private String        categoryName;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TicketResponse from(Ticket t) {
        TicketResponse r = new TicketResponse();
        r.setId(t.getId());
        r.setTicketNumber(t.getTicketNumber());
        r.setTitle(t.getTitle());
        r.setDescription(t.getDescription());
        r.setStatus(t.getStatus());
        r.setPriority(t.getPriority());
        r.setTags(t.getTags());
        r.setCreatedAt(t.getCreatedAt());
        r.setUpdatedAt(t.getUpdatedAt());
        if (t.getCreatedBy()  != null) r.setCreatedByName(t.getCreatedBy().getFullName());
        if (t.getAssignedTo() != null) r.setAssignedToName(t.getAssignedTo().getFullName());
        if (t.getCategory()   != null) r.setCategoryName(t.getCategory().getName());
        return r;
    }
}