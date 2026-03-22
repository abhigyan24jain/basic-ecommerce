package com.ecommerce.backend.dto;

import lombok.Data;

@Data
public class UserCreateDto {
    private String name;
    private String email;
    private String password;
    private String role; // Expecting "CUSTOMER", "VENDOR", or "ADMIN"
}