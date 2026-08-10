package com.voxcare.auth.model.dto;

import com.voxcare.auth.model.Role;

import java.time.LocalDateTime;

/**
 * Authentication response DTO containing JWT token and user information
 */
public class AuthResponse {

    private String token;
    private String refreshToken;
    private String email;
    private Role role;
    private String firstName;
    private String lastName;
    private LocalDateTime expiresAt;
    private String tokenType = "Bearer";

    // Constructors
    public AuthResponse() {}

    public AuthResponse(String token, String refreshToken, String email, Role role, 
                       String firstName, String lastName, LocalDateTime expiresAt) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.email = email;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.expiresAt = expiresAt;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
