package com.example.project.controller;

import com.example.project.model.AppUser;
import com.example.project.repository.AppUserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class AppUserController {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserController(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // List all users
    @GetMapping
    public String getAll(Model model) {
        List<AppUser> users = appUserRepository.findAll();
        model.addAttribute("users", users);
        return "users/list";
    }

    // Show create form
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        AppUser newUser = new AppUser();
        newUser.setActive(true); // Default to active
        model.addAttribute("user", newUser);
        return "users/form";
    }

    // Show edit form
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        model.addAttribute("user", user);
        return "users/form";
    }

    // Show user detail
    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        model.addAttribute("user", user);
        return "users/detail";
    }

    // Create new user (POST to /users/save or /users)
    @PostMapping("/save")
    public String create(@ModelAttribute AppUser request, @RequestParam(required = false) String password) {
        AppUser newUser = new AppUser();
        newUser.setFullName(request.getFullName());
        newUser.setEmail(request.getEmail());
        newUser.setRole(request.getRole());
        newUser.setActive(request.getActive());
        newUser.setCreatedAt(LocalDateTime.now());
        
        // Hash password if provided
        if (password != null && !password.isEmpty()) {
            newUser.setPassword(passwordEncoder.encode(password));
        }
        
        appUserRepository.save(newUser);
        return "redirect:/users";
    }

    // Alternative POST endpoint for simple form (if form uses action="/users")
    @PostMapping
    public String createPost(@ModelAttribute AppUser request, @RequestParam(required = false) String password) {
        return create(request, password);
    }

    // Update existing user (PUT to /users/update or /users/{id})
    @PostMapping("/update")
    public String update(@ModelAttribute AppUser request, @RequestParam(required = false) String password) {
        if (request.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required for update");
        }
        
        AppUser existingUser = appUserRepository.findById(request.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Update fields
        existingUser.setFullName(request.getFullName());
        existingUser.setEmail(request.getEmail());
        existingUser.setRole(request.getRole());
        existingUser.setActive(request.getActive());
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        // Update password only if provided
        if (password != null && !password.isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(password));
        }
        
        appUserRepository.save(existingUser);
        return "redirect:/users/" + request.getId();
    }

    // Alternative PUT endpoint for RESTful convention
    @PutMapping("/{id}")
    public String updatePut(@PathVariable Long id, @ModelAttribute AppUser request, @RequestParam(required = false) String password) {
        request.setId(id);
        return update(request, password);
    }

    // Delete user
    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        if (!appUserRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        appUserRepository.deleteById(id);
        return "redirect:/users";
    }

    // Alternative DELETE endpoint for RESTful convention
    @DeleteMapping("/{id}")
    public String deleteDelete(@PathVariable Long id) {
        return delete(id);
    }
}