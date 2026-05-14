package com.interviewdocs.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/auth")
public class AuthController {
    @GetMapping("/me")
     public Map<String, Object> me(Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            return Map.of(
                "authenticated", false
            );
        }

        return Map.of(
            "authenticated", true,
            "user", auth.getPrincipal()
        );
    }
    
}
