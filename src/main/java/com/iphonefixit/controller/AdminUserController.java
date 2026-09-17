package com.iphonefixit.controller;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.iphonefixit.dto.CreateSubAdminRequest;
import com.iphonefixit.entity.*;
import com.iphonefixit.repository.AdminUserRepository;

@RestController
@RequestMapping("/api/admin/subadmins")
@CrossOrigin(origins = "*")
public class AdminUserController {

    private final AdminUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(
            AdminUserRepository repository,
            PasswordEncoder passwordEncoder) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public AdminUser createSubAdmin(
            @RequestBody CreateSubAdminRequest request) {

        if (repository.existsByEmail(
                request.getEmail())) {

            throw new RuntimeException(
                "Email already exists");
        }

        AdminUser user =
            new AdminUser();

        user.setName(
            request.getName());

        user.setEmail(
            request.getEmail());

        user.setPassword(
            passwordEncoder.encode(
                request.getPassword()));

        user.setRole(
            Role.ROLE_SUBADMIN);

        user.setActive(true);

        return repository.save(user);
    }

    @GetMapping
    public List<AdminUser> getSubAdmins() {

        return repository.findAll()
                .stream()
                .filter(user ->
                    user.getRole()
                        == Role.ROLE_SUBADMIN)
                .toList();
    }

    @PutMapping("/{id}/status")
    public AdminUser changeStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {

        AdminUser user =
            repository.findById(id)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Subadmin not found"));

        user.setActive(active);

        return repository.save(user);
    }
}