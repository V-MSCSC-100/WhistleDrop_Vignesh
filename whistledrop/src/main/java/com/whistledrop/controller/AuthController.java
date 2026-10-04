package com.whistledrop.controller;

import com.whistledrop.dto.LoginRequest;
import com.whistledrop.dto.LoginResponse;
import com.whistledrop.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/login")
    LoginResponse login(@Valid @RequestBody LoginRequest request) { return authService.login(request); }
}
