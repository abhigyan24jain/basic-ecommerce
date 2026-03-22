package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.AuthRequestDto;
import com.ecommerce.backend.dto.AuthResponseDto;
import com.ecommerce.backend.dto.UserCreateDto;
import com.ecommerce.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for registering and logging in users")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user", description = "Creates an account. Does NOT return a JWT token.")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserCreateDto request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "Login user", description = "Authenticates user and returns a JWT token.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }
}