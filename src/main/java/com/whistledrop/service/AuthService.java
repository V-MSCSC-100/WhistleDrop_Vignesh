package com.whistledrop.service;

import com.whistledrop.dto.LoginRequest;
import com.whistledrop.dto.LoginResponse;
import com.whistledrop.exception.ApiException;
import com.whistledrop.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;
    private final long expirationMs;

    public AuthService(JwtService jwtService, PasswordEncoder passwordEncoder, @Value("${whistledrop.moderator.username}") String username, @Value("${whistledrop.moderator.password}") String password, @Value("${whistledrop.jwt.expiration-ms}") long expirationMs) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
        this.expirationMs = expirationMs;
    }

    public LoginResponse login(LoginRequest request) {
        if (!username.equals(request.username()) || !passwordEncoder.matches(request.password(), passwordEncoder.encode(password))) throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        return new LoginResponse(jwtService.generateToken(username), "Bearer", expirationMs / 1000);
    }
}
