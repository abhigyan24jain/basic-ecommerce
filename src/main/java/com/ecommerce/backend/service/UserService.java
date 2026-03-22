package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.UserCreateDto;
import com.ecommerce.backend.dto.UserResponseDto;
import com.ecommerce.backend.entity.Role;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto createUser(UserCreateDto createDto) {
        if (userRepository.existsByEmail(createDto.getEmail())) {
            throw new RuntimeException("Email is already registered!");
            // Better practice: Create a custom DuplicateResourceException like we did in Phase 2
        }

        User user = new User();
        user.setName(createDto.getName());
        user.setEmail(createDto.getEmail());
        user.setPassword(createDto.getPassword()); // Needs hashing in prod
        user.setRole(Role.valueOf(createDto.getRole().toUpperCase()));

        User savedUser = userRepository.save(user);
        return mapToResponseDto(savedUser);
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToResponseDto(user);
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // Helper method to keep code clean
    private UserResponseDto mapToResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}