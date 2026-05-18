package com.example.project.controller;

import com.example.project.dto.CreateCommentRequest;
import com.example.project.model.AppUser;
import com.example.project.model.Ticket;
import com.example.project.model.TicketComment;
import com.example.project.model.UserRole;
import com.example.project.repository.TicketCommentRepository;
import com.example.project.repository.TicketRepository;
import com.example.project.service.TicketService;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/comments")
public class TicketCommentController {

    private final TicketCommentRepository ticketCommentRepository;
    private final TicketRepository ticketRepository;
    private final TicketService ticketService;

    public TicketCommentController(TicketCommentRepository ticketCommentRepository,
                                   TicketRepository ticketRepository,
                                   TicketService ticketService) {
        this.ticketCommentRepository = ticketCommentRepository;
        this.ticketRepository = ticketRepository;
        this.ticketService = ticketService;
    }

    private AppUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (AppUser) auth.getPrincipal();
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER_SUPPORT')")
    public String showCreateForm(Model model) {
        model.addAttribute("comment", new TicketComment());
        model.addAttribute("tickets", ticketRepository.findAll());
        model.addAttribute("currentUser", getCurrentUser());
        return "comments/form";
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER_SUPPORT')")
    public String getAll(Model model) {
        model.addAttribute("comments", ticketCommentRepository.findAll());
        return "comments/list";
    }

    @GetMapping("/ticket/{ticketId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER_SUPPORT')")
    public String getForTicket(@PathVariable Long ticketId, Model model) {
        model.addAttribute("comments", ticketCommentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId));
        model.addAttribute("ticketId", ticketId);
        return "comments/list-for-ticket";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER_SUPPORT', 'CUSTOMER')")
    public String create(@ModelAttribute CreateCommentRequest request) {
        AppUser author = getCurrentUser();

        ticketService.addReply(request.getTicketId(), author, request.getMessage());
        return "redirect:/tickets/" + request.getTicketId();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER_SUPPORT')")
    public String delete(@PathVariable Long id) {
        if (!ticketCommentRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }
        ticketCommentRepository.deleteById(id);
        return "redirect:/comments";
    }
}
