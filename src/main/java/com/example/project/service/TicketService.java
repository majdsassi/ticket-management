package com.example.project.service;

import com.example.project.model.AppUser;
import com.example.project.model.Project;
import com.example.project.model.Ticket;
import com.example.project.model.TicketPriority;
import com.example.project.model.TicketStatus;
import com.example.project.model.UserRole;
import com.example.project.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Create a new ticket
     */
    public Ticket createTicket(String title, String description, TicketPriority priority,
                               Project project, AppUser reporter) {
        Ticket ticket = Ticket.builder()
                .title(title)
                .description(description)
                .priority(priority)
                .status(TicketStatus.OPEN)
                .project(project)
                .reporter(reporter)
                .build();
        return ticketRepository.save(ticket);
    }

    /**
     * Get ticket by ID
     */
    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    /**
     * Get all tickets
     */
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    /**
     * Get tickets by status
     */
    public List<Ticket> getTicketsByStatus(TicketStatus status) {
        return ticketRepository.findByStatus(status);
    }

    /**
     * Get tickets assigned to a user
     */
    public List<Ticket> getAssignedTickets(AppUser assignee) {
        return ticketRepository.findByAssignee(assignee);
    }

    /**
     * Get tickets reported by a user
     */
    public List<Ticket> getReportedTickets(AppUser reporter) {
        return ticketRepository.findByReporter(reporter);
    }

    /**
     * Get tickets for a project
     */
    public List<Ticket> getProjectTickets(Project project) {
        return ticketRepository.findByProject(project);
    }

    /**
     * Assign ticket to a user
     */
    public Ticket assignTicket(Long ticketId, AppUser assignee) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        ticket.setAssignee(assignee);
        return ticketRepository.save(ticket);
    }

    /**
     * Unassign ticket
     */
    public Ticket unassignTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        ticket.setAssignee(null);
        return ticketRepository.save(ticket);
    }

    /**
     * Update ticket status
     */
    public Ticket updateStatus(Long ticketId, TicketStatus status) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        ticket.setStatus(status);
        return ticketRepository.save(ticket);
    }

    /**
     * Update ticket priority
     */
    public Ticket updatePriority(Long ticketId, TicketPriority priority) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        ticket.setPriority(priority);
        return ticketRepository.save(ticket);
    }

    /**
     * Update ticket details
     */
    public Ticket updateTicket(Long ticketId, String title, String description) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        ticket.setTitle(title);
        ticket.setDescription(description);
        return ticketRepository.save(ticket);
    }

    /**
     * Check if user can edit ticket
     */
    public boolean canEditTicket(AppUser user, Ticket ticket) {
        return user.getRole() == UserRole.ADMIN ||
                ticket.getReporter().getId().equals(user.getId()) ||
                ticket.getAssignee() != null && ticket.getAssignee().getId().equals(user.getId()) ||
                user.getRole() == UserRole.CUSTOMER_SUPPORT;
    }

    /**
     * Get open tickets count
     */
    public long getOpenTicketsCount() {
        return ticketRepository.countByStatus(TicketStatus.OPEN);
    }

    /**
     * Get pending tickets for support team
     */
    public List<Ticket> getPendingTickets() {
           return ticketRepository.findByStatusIn(List.of(TicketStatus.OPEN, TicketStatus.IN_PROGRESS));
    }
}
