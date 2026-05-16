package com.example.project.controller;

import com.example.project.model.UserRole;
import com.example.project.service.ProjectService;
import com.example.project.service.TicketService;
import com.example.project.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.project.model.AppUser;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final TicketService ticketService;
    private final ProjectService projectService;
    private final UserService userService;

    public DashboardController(TicketService ticketService, ProjectService projectService, UserService userService) {
        this.ticketService = ticketService;
        this.projectService = projectService;
        this.userService = userService;
    }

    @GetMapping
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUser currentUser = (AppUser) auth.getPrincipal();

        model.addAttribute("user", currentUser);
        model.addAttribute("role", currentUser.getRole());

        if (currentUser.getRole() == UserRole.ADMIN) {
            return adminDashboard(model);
        } else if (currentUser.getRole() == UserRole.CUSTOMER_SUPPORT) {
            return supportDashboard(model, currentUser);
        } else {
            return customerDashboard(model, currentUser);
        }
    }

    private String adminDashboard(Model model) {
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("totalProjects", projectService.getProjectCount());
        model.addAttribute("openTickets", ticketService.getOpenTicketsCount());
        model.addAttribute("allTickets", ticketService.getAllTickets());
        model.addAttribute("projects", projectService.getAllProjects());
        model.addAttribute("allUsers", userService.getAllUsers());
        return "dashboard/admin-dashboard";
    }

    private String supportDashboard(Model model, AppUser currentUser) {
        model.addAttribute("pendingTickets", ticketService.getPendingTickets());
        model.addAttribute("assignedTickets", ticketService.getAssignedTickets(currentUser));
        model.addAttribute("allTickets", ticketService.getAllTickets());
        model.addAttribute("projects", projectService.getAllProjects());
        return "dashboard/support-dashboard";
    }

    private String customerDashboard(Model model, AppUser currentUser) {
        model.addAttribute("myTickets", ticketService.getReportedTickets(currentUser));
        model.addAttribute("projects", projectService.getAllProjects());
        return "dashboard/customer-dashboard";
    }
}
