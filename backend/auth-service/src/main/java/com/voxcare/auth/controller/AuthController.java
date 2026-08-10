package com.voxcare.auth.controller;

import com.voxcare.auth.model.dto.AuthResponse;
import com.voxcare.auth.model.dto.LoginRequest;
import com.voxcare.auth.model.dto.RegisterRequest;
import com.voxcare.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller for user authentication and registration
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * User registration endpoint
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * User login endpoint
     */
    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates user and returns JWT tokens")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Token refresh endpoint
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Refreshes the access token using a valid refresh token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam String refreshToken) {
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Returns service health status")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth service is healthy");
    }
}
