package com.example.project.dto;

import com.example.project.model.TicketPriority;
import com.example.project.model.TicketStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTicketRequest {
    private String title;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private Long projectId;
    private Long reporterId;
    private Long assigneeId;
}
