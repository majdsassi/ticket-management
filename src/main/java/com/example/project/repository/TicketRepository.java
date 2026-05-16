package com.example.project.repository;

import com.example.project.model.Ticket;
import com.example.project.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByProjectId(Long projectId);

    List<Ticket> findByAssigneeId(Long assigneeId);
}
