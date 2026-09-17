package com.iphonefixit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.iphonefixit.entity.AdminUser;
import com.iphonefixit.entity.Role;
import com.iphonefixit.repository.AdminUserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

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

        AdminUser admin = repository
                .findByEmail(adminEmail)
                .orElseGet(AdminUser::new);

        admin.setName(adminName);
        admin.setEmail(adminEmail);

        admin.setPassword(
                passwordEncoder.encode(adminPassword)
        );

        admin.setRole(Role.ROLE_ADMIN);
        admin.setActive(true);

        repository.save(admin);

        System.out.println(
                "Admin account initialized: " + adminEmail
        );
    }
}