package com.example.project.controller;

import com.example.project.dto.CreateTicketRequest;
import com.example.project.dto.UpdateTicketRequest;
import com.example.project.model.AppUser;
import com.example.project.model.Project;
import com.example.project.model.Ticket;
import com.example.project.model.TicketStatus;
import com.example.project.repository.AppUserRepository;
import com.example.project.repository.ProjectRepository;
import com.example.project.repository.TicketRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;
    private final AppUserRepository appUserRepository;

    public TicketController(TicketRepository ticketRepository,
                            ProjectRepository projectRepository,
                            AppUserRepository appUserRepository) {
        this.ticketRepository = ticketRepository;
        this.projectRepository = projectRepository;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("projects", projectRepository.findAll());
        model.addAttribute("users", appUserRepository.findAll());
        return "tickets/form";
    }

    @GetMapping
    public String getAll(@RequestParam(required = false) TicketStatus status,
                         @RequestParam(required = false) Long projectId,
                         @RequestParam(required = false) Long assigneeId,
                         Model model) {
        List<Ticket> tickets;
        if (status != null) {
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
        model.addAttribute("users", appUserRepository.findAll());
        return "tickets/list";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
        model.addAttribute("ticket", ticket);
        return "tickets/detail";
    }

        @PostMapping
        public String create(@ModelAttribute CreateTicketRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        AppUser reporter = appUserRepository.findById(request.getReporterId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporter not found"));

        AppUser assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = appUserRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found"));
        }

        Ticket ticket = Ticket.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .status(request.getStatus() == null ? TicketStatus.OPEN : request.getStatus())
            .priority(request.getPriority())
            .project(project)
            .reporter(reporter)
            .assignee(assignee)
            .build();

        ticketRepository.save(ticket);
        return "redirect:/tickets";
        }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute UpdateTicketRequest request) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            ticket.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            ticket.setPriority(request.getPriority());
        }

        if (request.getAssigneeId() != null) {
            AppUser assignee = appUserRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found"));
            ticket.setAssignee(assignee);
        } else {
            ticket.setAssignee(null);
        }

        ticketRepository.save(ticket);
        return "redirect:/tickets/" + id;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
        }
        ticketRepository.deleteById(id);
        return "redirect:/tickets";
    }
}
