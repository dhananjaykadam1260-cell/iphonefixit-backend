package com.iphonefixit.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.iphonefixit.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .cors(cors ->
                cors.configurationSource(corsConfigurationSource())
            )

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                // PUBLIC APIs
                .requestMatchers(
                    "/api/auth/**",
                    "/api/repairs/phone/**",
                    "/api/bills/**",
                    "/api/store",
                    "/api/items",
                    "/api/items/**",
                    "/api/sales/*/bill",
                    "/uploads/**"
                )
                .permitAll()

                // ADMIN ONLY
                .requestMatchers(
                    "/api/admin/**"
                )
                .hasRole("ADMIN")

                // ADMIN + SUBADMIN
                .requestMatchers(
                    "/api/dashboard/**",
                    "/api/customers/**",
                    "/api/repairs/**",
                    "/api/whatsapp/**",
                    "/api/sales/**"
                )
                .hasAnyRole(
                    "ADMIN",
                    "SUBADMIN"
                )

                .anyRequest()
                .authenticated()
            )

            .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config =
                new CorsConfiguration();

        config.setAllowedOrigins(
            List.of(
                "http://localhost:5173",
                "https://iphonefixit-frontend.onrender.com"
            )
        );

        config.setAllowedMethods(
            List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
            )
        );

        config.setAllowedHeaders(
            List.of("*")
        );

        config.setExposedHeaders(
            List.of("Authorization")
        );

        config.setAllowCredentials(true);

        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                config
        );

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration
                .getAuthenticationManager();
    }
}