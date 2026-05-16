package com.example.project.controller;

import com.example.project.dto.CreateTicketRequest;
import com.example.project.dto.UpdateTicketRequest;
import com.example.project.model.AppUser;
import com.example.project.model.Project;
import com.example.project.model.Ticket;
import com.example.project.model.TicketPriority;
import com.example.project.model.TicketStatus;
import com.example.project.model.UserRole;
import com.example.project.repository.AppUserRepository;
import com.example.project.repository.ProjectRepository;
import com.example.project.repository.TicketRepository;
import com.example.project.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketRepository ticketRepository;
    private final TicketService ticketService;
    private final ProjectRepository projectRepository;
    private final AppUserRepository appUserRepository;

    public TicketController(TicketRepository ticketRepository,
                            TicketService ticketService,
                            ProjectRepository projectRepository,
                            AppUserRepository appUserRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketService = ticketService;
        this.projectRepository = projectRepository;
        this.appUserRepository = appUserRepository;
    }

    private AppUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (AppUser) auth.getPrincipal();
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        AppUser currentUser = getCurrentUser();
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("projects", projectRepository.findAll());
        model.addAttribute("currentUser", currentUser);
        if (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.CUSTOMER_SUPPORT) {
            model.addAttribute("users", appUserRepository.findAll());
        }
        model.addAttribute("priorities", TicketPriority.values());
        return "tickets/form";
    }

    @GetMapping
    public String getAll(@RequestParam(required = false) TicketStatus status,
                         @RequestParam(required = false) Long projectId,
                         @RequestParam(required = false) Long assigneeId,
                         Model model) {
        AppUser currentUser = getCurrentUser();
        List<Ticket> tickets;
        
        if (currentUser.getRole() == UserRole.CUSTOMER) {
            tickets = ticketService.getReportedTickets(currentUser);
        } else if (status != null) {
            tickets = ticketRepository.findByStatus(status);
        } else if (projectId != null) {
            tickets = ticketRepository.findByProjectId(projectId);
        } else if (assigneeId != null) {
            tickets = ticketRepository.findByAssigneeId(assigneeId);
        } else {
            tickets = ticketRepository.findAll();
        }
        
        model.addAttribute("tickets", tickets);
        model.addAttribute("projects", projectRepository.findAll());
        model.addAttribute("statuses", TicketStatus.values());
        model.addAttribute("currentUser", currentUser);
        return "tickets/list";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
        AppUser currentUser = getCurrentUser();
        model.addAttribute("ticket", ticket);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("canEdit", ticketService.canEditTicket(currentUser, ticket));
        model.addAttribute("statuses", TicketStatus.values());
        model.addAttribute("priorities", TicketPriority.values());
        if (currentUser.getRole() != UserRole.CUSTOMER) {
            model.addAttribute("users", appUserRepository.findAll());
        }
        return "tickets/detail";
    }

    @PostMapping
    public String create(@RequestParam String title,
                        @RequestParam String description,
                        @RequestParam Long projectId,
                        @RequestParam(required = false) TicketPriority priority,
                        Model model) {
        try {
            AppUser currentUser = getCurrentUser();
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

            if (priority == null) {
                priority = TicketPriority.MEDIUM;
            }

            Ticket ticket = ticketService.createTicket(title, description, priority, project, currentUser);
            return "redirect:/tickets/" + ticket.getId();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("projects", projectRepository.findAll());
            model.addAttribute("priorities", TicketPriority.values());
            return "tickets/form";
        }
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                        @RequestParam String title,
                        @RequestParam String description,
                        @RequestParam TicketStatus status,
                        @RequestParam TicketPriority priority,
                        @RequestParam(required = false) Long assigneeId,
                        Model model) {
        try {
            Ticket ticket = ticketRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
            
            AppUser currentUser = getCurrentUser();
            if (!ticketService.canEditTicket(currentUser, ticket)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot edit this ticket");
            }

            ticket.setTitle(title);
            ticket.setDescription(description);
            ticket.setStatus(status);
            ticket.setPriority(priority);

            if (assigneeId != null && (currentUser.getRole() == UserRole.ADMIN || 
                    currentUser.getRole() == UserRole.CUSTOMER_SUPPORT)) {
                AppUser assignee = appUserRepository.findById(assigneeId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found"));
                ticket.setAssignee(assignee);
            }

            ticketRepository.save(ticket);
            return "redirect:/tickets/" + id;
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/tickets/" + id;
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
        
        AppUser currentUser = getCurrentUser();
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can delete tickets");
        }
        
        ticketRepository.deleteById(id);
        return "redirect:/tickets";
    }
}
