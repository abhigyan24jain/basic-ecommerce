package com.ecommerce.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateDto {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(ADMIN|VENDOR|CUSTOMER)$", message = "Role must be ADMIN, VENDOR, or CUSTOMER")
    private String role;
}