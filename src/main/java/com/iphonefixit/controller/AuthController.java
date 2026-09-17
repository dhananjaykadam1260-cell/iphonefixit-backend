package com.iphonefixit.controller;

import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

import com.iphonefixit.dto.*;
import com.iphonefixit.entity.AdminUser;
import com.iphonefixit.repository.AdminUserRepository;
import com.iphonefixit.service.JwtService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AdminUserRepository userRepository;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationManager authenticationManager,
            AdminUserRepository userRepository,
            JwtService jwtService) {

        this.authenticationManager =
                authenticationManager;

        this.userRepository =
                userRepository;

        this.jwtService =
                jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request) {

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        AdminUser user =
            userRepository
                .findByEmail(request.getEmail())
                .orElseThrow();

        String token =
            jwtService.generateToken(user);

        return new LoginResponse(
            token,
            user.getName(),
            user.getEmail(),
            user.getRole().name()
        );
    }
}