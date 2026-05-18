package com.example.project.service;

import com.example.project.model.AppUser;
import com.example.project.model.Project;
import com.example.project.model.Ticket;
import com.example.project.model.TicketPriority;
import com.example.project.model.TicketStatus;
import com.example.project.model.UserRole;
import com.example.project.model.TicketComment;
import com.example.project.repository.TicketCommentRepository;
import com.example.project.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketCommentRepository ticketCommentRepository;

    public TicketService(TicketRepository ticketRepository, TicketCommentRepository ticketCommentRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketCommentRepository = ticketCommentRepository;
    }

    /**
     * Create a new ticket
     */
    public Ticket createTicket(String title, String description, TicketPriority priority,
                               Project project, AppUser reporter) {
        if (reporter == null || reporter.getRole() != UserRole.CUSTOMER) {
            throw new IllegalArgumentException("Only customers can create tickets");
        }

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
     * Add a reply to a ticket and adjust status based on who replied.
     */
    public TicketComment addReply(Long ticketId, AppUser author, String message) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        if (author == null) {
            throw new IllegalArgumentException("Author is required");
        }

        boolean isSupport = author.getRole() == UserRole.ADMIN || author.getRole() == UserRole.CUSTOMER_SUPPORT;
        boolean isReporter = author.getRole() == UserRole.CUSTOMER && ticket.getReporter() != null
                && ticket.getReporter().getId().equals(author.getId());

        if (!isSupport && !isReporter) {
            throw new IllegalArgumentException("You can only reply to your own ticket");
        }

        if (ticket.getStatus() == TicketStatus.CLOSED && !isReporter) {
            throw new IllegalArgumentException("Closed tickets can only be reopened by the reporter");
        }

        TicketComment comment = TicketComment.builder()
                .ticket(ticket)
                .author(author)
                .message(message)
                .build();

        if (isSupport && ticket.getStatus() == TicketStatus.OPEN) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        } else if (isReporter && (ticket.getStatus() == TicketStatus.RESOLVED || ticket.getStatus() == TicketStatus.CLOSED)) {
            ticket.setStatus(TicketStatus.OPEN);
        }

        ticketCommentRepository.save(comment);
        ticketRepository.save(ticket);
        return comment;
    }

    /**
     * Change the ticket status with role-aware transition rules.
     */
    public Ticket changeStatus(Long ticketId, AppUser actor, TicketStatus newStatus) {
        if (actor == null || (actor.getRole() != UserRole.ADMIN && actor.getRole() != UserRole.CUSTOMER_SUPPORT)) {
            throw new IllegalArgumentException("Only support users can change ticket status");
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        if (newStatus == null) {
            throw new IllegalArgumentException("Status is required");
        }

        if (actor.getRole() != UserRole.ADMIN && !isAllowedTransition(ticket.getStatus(), newStatus)) {
            throw new IllegalArgumentException("Invalid status transition from " + ticket.getStatus() + " to " + newStatus);
        }

        ticket.setStatus(newStatus);
        return ticketRepository.save(ticket);
    }

    private boolean isAllowedTransition(TicketStatus currentStatus, TicketStatus nextStatus) {
        return switch (currentStatus) {
            case OPEN -> nextStatus == TicketStatus.IN_PROGRESS || nextStatus == TicketStatus.RESOLVED || nextStatus == TicketStatus.CLOSED;
            case IN_PROGRESS -> nextStatus == TicketStatus.RESOLVED || nextStatus == TicketStatus.CLOSED;
            case RESOLVED -> nextStatus == TicketStatus.CLOSED || nextStatus == TicketStatus.IN_PROGRESS;
            case CLOSED -> false;
        };
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
