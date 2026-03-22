package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.UserDeleteRequestDto;
import com.ecommerce.backend.dto.UserResponseDto;
import com.ecommerce.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get user by ID", description = "Fetches user details without password.")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Get all users", description = "Fetches all registered users.")
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @Operation(summary = "Self-Delete Account", description = "Allows a user to permanently delete their account by verifying their email and password.")
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMyAccount(@RequestBody UserDeleteRequestDto request) {
        return ResponseEntity.ok(userService.deleteOwnAccount(request));
    }

    @Operation(summary = "Delete User (ADMIN ONLY)", description = "Allows an Admin to immediately delete any user by ID without a password.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserAsAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteUserAsAdmin(id));
    }
}