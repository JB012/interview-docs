package com.interviewdocs.server.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/").permitAll()
                .anyRequest().authenticated())
            .oauth2Client(oauth2 -> {})
            .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("http://localhost:4200/home", 
            true))
            .csrf(csrf -> csrf
                .csrfTokenRepository(
                    CookieCsrfTokenRepository.withHttpOnlyFalse()
                )
            )
            .logout(logout -> logout
                .logoutSuccessUrl("http://localhost:4200")
            );
            
        return http.build();
    }
}