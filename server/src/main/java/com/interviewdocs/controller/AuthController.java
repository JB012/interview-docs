package com.interviewdocs.server.controller;

import java.util.Map;

import java.security.Principal;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.utils.SecurityService;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import java.util.Optional;

import io.micronaut.http.annotation.*;

@Controller
@Secured(SecurityRule.IS_ANONYMOUS)
public class AuthController {

    private final SecurityService securityService;

    public AuthController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Get("/auth/me")
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
