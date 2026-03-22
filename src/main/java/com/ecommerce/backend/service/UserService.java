package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.UserDeleteRequestDto;
import com.ecommerce.backend.dto.UserResponseDto;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToResponseDto(user);
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(User::isActive)
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public String deleteOwnAccount(UserDeleteRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password. Account deletion denied.");
        }

        // INDUSTRY STANDARD: Soft Delete & Anonymize Data (GDPR Compliant)
        anonymizeAndDeactivateUser(user);

        return "Your account has been successfully deleted and anonymized.";
    }

    @Transactional
    public String deleteUserAsAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // INDUSTRY STANDARD: Soft Delete & Anonymize Data
        anonymizeAndDeactivateUser(user);

        return "User account permanently disabled and anonymized by Admin.";
    }

    // --- PRIVATE HELPER METHOD ---
    private void anonymizeAndDeactivateUser(User user) {
        // 1. Scramble personal info so it cannot be traced back to the human
        user.setName("Deleted User");

        // Append the ID to a fake email so the unique database constraint isn't violated
        // and they can reuse their real email in the future if they want.
        user.setEmail("deleted_" + user.getId() + "@anonymized.com");

        // 2. Destroy the password hash so the account can never be accessed
        user.setPassword(passwordEncoder.encode("DELETED_ACCOUNT_LOCKED"));

        // 3. Mark as inactive (Spring Security will now block any login attempts)
        user.setActive(false);

        // 4. Save the changes instead of deleting the row!
        userRepository.save(user);
    }

    // Helper method
    private UserResponseDto mapToResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        return dto;
    }
}