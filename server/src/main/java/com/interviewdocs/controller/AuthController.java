package com.interviewdocs.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.utils.SecurityService;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import java.util.Optional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@Secured(SecurityRule.IS_ANONYMOUS)
public class AuthController {

    private final SecurityService securityService;

    public AuthController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping("/auth/me")
    public Map<String, Object> me() {
        Optional<Authentication> auth = securityService.getAuthentication();
        
        if (auth == null || !auth.isPresent()) {
            return Map.of(
            "authenticated", false
            );
        }

        return Map.of(
        "authenticated", true,
        "user", auth.get().getName()
        );
    }
}
