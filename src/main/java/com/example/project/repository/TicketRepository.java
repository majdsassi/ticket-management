package com.example.project.repository;

import com.example.project.model.AppUser;
import com.example.project.model.Project;
import com.example.project.model.Ticket;
import com.example.project.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByStatusIn(List<TicketStatus> statuses);
    List<Ticket> findByProjectId(Long projectId);
    List<Ticket> findByAssigneeId(Long assigneeId);
    List<Ticket> findByAssignee(AppUser assignee);
    List<Ticket> findByReporter(AppUser reporter);
    List<Ticket> findByProject(Project project);
    long countByStatus(TicketStatus status);
}
