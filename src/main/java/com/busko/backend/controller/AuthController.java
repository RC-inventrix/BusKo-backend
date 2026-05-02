package com.busko.backend.controller;

import com.busko.backend.entity.Role;
import com.busko.backend.service.AuthService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint: POST /api/auth/init?role=DRIVER or PASSENGER
     */
    @PostMapping("/init")
    public ResponseEntity<Map<String, String>> initDevice(@RequestParam Role role) {
        Map<String, String> credentials = authService.initAnonymousUser(role);
        return ResponseEntity.ok(credentials);
    }
}