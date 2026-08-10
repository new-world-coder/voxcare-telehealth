package com.voxcare.auth.service;

import com.voxcare.auth.model.Role;
import com.voxcare.auth.model.User;
import com.voxcare.auth.model.dto.AuthResponse;
import com.voxcare.auth.model.dto.LoginRequest;
import com.voxcare.auth.model.dto.RegisterRequest;
import com.voxcare.auth.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Authentication service for user management and authentication
 */
@Service
@Transactional
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder, 
                      JwtService jwtService, 
                      AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Register a new user
     */
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        
        // Set additional user info (this would typically come from a separate profile service)
        // For demo purposes, we'll store it in the user entity
        
        user = userRepository.save(user);

        // Generate tokens
        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(
                token,
                refreshToken,
                user.getEmail(),
                user.getRole(),
                request.getFirstName(),
                request.getLastName(),
                LocalDateTime.now().plusSeconds(jwtService.getJwtExpiration() / 1000)
        );
    }

    /**
     * Authenticate user and return tokens
     */
    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Generate tokens
        String token = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new AuthResponse(
                token,
                refreshToken,
                user.getEmail(),
                user.getRole(),
                "User", // This would come from a profile service
                "Name",  // This would come from a profile service
                LocalDateTime.now().plusSeconds(jwtService.getJwtExpiration() / 1000)
        );
    }

    /**
     * Refresh access token
     */
    public AuthResponse refreshToken(String refreshToken) {
        // Validate refresh token
        if (!jwtService.isTokenValid(refreshToken, loadUserByUsername(jwtService.extractUsername(refreshToken)))) {
            throw new RuntimeException("Invalid refresh token");
        }

        UserDetails userDetails = loadUserByUsername(jwtService.extractUsername(refreshToken));
        String newToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new AuthResponse(
                newToken,
                newRefreshToken,
                user.getEmail(),
                user.getRole(),
                "User", // This would come from a profile service
                "Name",  // This would come from a profile service
                LocalDateTime.now().plusSeconds(jwtService.getJwtExpiration() / 1000)
        );
    }

    /**
     * Load user by username (email)
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    /**
     * Get all users (admin only)
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    /**
     * Check if user exists
     */
    public boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
