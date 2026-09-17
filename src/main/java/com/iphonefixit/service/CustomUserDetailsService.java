package com.iphonefixit.service;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.iphonefixit.repository.AdminUserRepository;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final AdminUserRepository repository;

    public CustomUserDetailsService(
            AdminUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(
            String email)
            throws UsernameNotFoundException {

        return repository.findByEmail(email)
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "User not found"));
    }
}