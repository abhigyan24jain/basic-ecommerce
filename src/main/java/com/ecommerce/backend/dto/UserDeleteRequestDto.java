package com.ecommerce.backend.dto;

import lombok.Data;

@Data
public class UserDeleteRequestDto {
    private String email;
    private String password;
}