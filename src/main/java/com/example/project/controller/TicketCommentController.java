package com.example.project.controller;

import com.example.project.dto.CreateCommentRequest;
import com.example.project.model.AppUser;
import com.example.project.model.Ticket;
import com.example.project.model.TicketComment;
import com.example.project.repository.AppUserRepository;
import com.example.project.repository.TicketCommentRepository;
import com.example.project.repository.TicketRepository;

import org.springframework.http.HttpStatus;
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
    private final AppUserRepository appUserRepository;

    public TicketCommentController(TicketCommentRepository ticketCommentRepository,
                                   TicketRepository ticketRepository,
                                   AppUserRepository appUserRepository) {
        this.ticketCommentRepository = ticketCommentRepository;
        this.ticketRepository = ticketRepository;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("comment", new TicketComment());
        model.addAttribute("tickets", ticketRepository.findAll());
        model.addAttribute("users", appUserRepository.findAll());
        return "comments/form";
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("comments", ticketCommentRepository.findAll());
        return "comments/list";
    }

    @GetMapping("/ticket/{ticketId}")
    public String getForTicket(@PathVariable Long ticketId, Model model) {
        model.addAttribute("comments", ticketCommentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId));
        model.addAttribute("ticketId", ticketId);
        return "comments/list-for-ticket";
    }

        @PostMapping
        public String create(@ModelAttribute CreateCommentRequest request) {
        Ticket ticket = ticketRepository.findById(request.getTicketId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        AppUser author = appUserRepository.findById(request.getAuthorId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));

        TicketComment comment = TicketComment.builder()
            .message(request.getMessage())
            .ticket(ticket)
            .author(author)
            .build();

        ticketCommentRepository.save(comment);
        return "redirect:/tickets/" + request.getTicketId();
        }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        if (!ticketCommentRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }
        ticketCommentRepository.deleteById(id);
        return "redirect:/comments";
    }
}
