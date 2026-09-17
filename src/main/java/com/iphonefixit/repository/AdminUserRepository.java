package com.iphonefixit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iphonefixit.entity.AdminUser;

public interface AdminUserRepository
        extends JpaRepository<AdminUser, Long> {

    Optional<AdminUser> findByEmail(String email);

    boolean existsByEmail(String email);
}