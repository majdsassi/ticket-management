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

import java.util.List;

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
        List<AppUser> allUsers = userService.getAllUsers();
        var allTickets = ticketService.getAllTickets();

        long customerCount = allUsers.stream().filter(u -> u.getRole() == UserRole.CUSTOMER).count();
        long supportCount = allUsers.stream().filter(u -> u.getRole() == UserRole.CUSTOMER_SUPPORT).count();
        long adminCount = allUsers.stream().filter(u -> u.getRole() == UserRole.ADMIN).count();

        long openTicketCount = allTickets.stream().filter(t -> t.getStatus().name().equals("OPEN")).count();
        long inProgressTicketCount = allTickets.stream().filter(t -> t.getStatus().name().equals("IN_PROGRESS")).count();
        long resolvedTicketCount = allTickets.stream().filter(t -> t.getStatus().name().equals("RESOLVED")).count();

        model.addAttribute("totalUsers", allUsers.size());
        model.addAttribute("totalProjects", projectService.getProjectCount());
        model.addAttribute("openTickets", ticketService.getOpenTicketsCount());
        model.addAttribute("allTickets", allTickets);
        model.addAttribute("projects", projectService.getAllProjects());
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("customerCount", customerCount);
        model.addAttribute("supportCount", supportCount);
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("openTicketCount", openTicketCount);
        model.addAttribute("inProgressTicketCount", inProgressTicketCount);
        model.addAttribute("resolvedTicketCount", resolvedTicketCount);
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
        var myTickets = ticketService.getReportedTickets(currentUser);
        long myOpenTickets = myTickets.stream().filter(t -> t.getStatus().name().equals("OPEN")).count();
        long myResolvedTickets = myTickets.stream().filter(t -> t.getStatus().name().equals("RESOLVED")).count();

        model.addAttribute("myTickets", myTickets);
        model.addAttribute("myOpenTickets", myOpenTickets);
        model.addAttribute("myResolvedTickets", myResolvedTickets);
        model.addAttribute("projects", projectService.getAllProjects());
        return "dashboard/customer-dashboard";
    }
}
