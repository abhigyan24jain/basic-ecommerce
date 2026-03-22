package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.CategoryDto;
import com.ecommerce.backend.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management APIs")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Create a Category (ADMIN ONLY)", description = "Only users with the ADMIN role can access this.")
    @PreAuthorize("hasRole('ADMIN')") // RBAC Enforcement
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto dto) {
        return new ResponseEntity<>(categoryService.createCategory(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Get all categories", description = "Anyone with a valid token can view categories.")
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}