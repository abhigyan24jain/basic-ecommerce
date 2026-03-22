package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query to find a user by email (useful for login later)
    Optional<User> findByEmail(String email);

    // Check if email exists to prevent duplicates
    boolean existsByEmail(String email);
}