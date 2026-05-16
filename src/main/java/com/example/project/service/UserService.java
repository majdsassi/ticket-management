package com.example.project.service;

import com.example.project.model.AppUser;
import com.example.project.model.UserRole;
import com.example.project.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user
     */
    public AppUser registerUser(String fullName, String email, String password, UserRole role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        AppUser user = AppUser.builder()
                .fullName(fullName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role != null ? role : UserRole.CUSTOMER)
                .active(true)
                .build();

        return userRepository.save(user);
    }

    /**
     * Find user by email for authentication
     */
    public Optional<AppUser> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Find user by ID
     */
    public Optional<AppUser> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Get all users
     */
    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get all users by role
     */
    public List<AppUser> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    /**
     * Update user details
     */
    public AppUser updateUser(Long id, String fullName, String email) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setFullName(fullName);
        user.setEmail(email);
        return userRepository.save(user);
    }

    /**
     * Change user password
     */
    public void changePassword(Long id, String oldPassword, String newPassword) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Deactivate user account
     */
    public void deactivateUser(Long id) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setActive(false);
        userRepository.save(user);
    }

    /**
     * Activate user account
     */
    public void activateUser(Long id) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setActive(true);
        userRepository.save(user);
    }

    /**
     * Check if email exists
     */
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * Verify user password
     */
    public boolean verifyPassword(AppUser user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
