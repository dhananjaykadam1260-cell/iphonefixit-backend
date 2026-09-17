package com.iphonefixit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.iphonefixit.entity.*;
import com.iphonefixit.repository.AdminUserRepository;

@Component
public class DataInitializer
        implements CommandLineRunner {

    private final AdminUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.name}")
    private String adminName;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    public DataInitializer(
            AdminUserRepository repository,
            PasswordEncoder passwordEncoder) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (!repository.existsByEmail(adminEmail)) {

            AdminUser admin =
                new AdminUser();

            admin.setName(adminName);
            admin.setEmail(adminEmail);

            admin.setPassword(
                passwordEncoder.encode(
                    adminPassword));

            admin.setRole(
                Role.ROLE_ADMIN);

            admin.setActive(true);

            repository.save(admin);
        }
    }
}