package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.ProductDto;
import com.ecommerce.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products & Inventory", description = "Manage products and stock levels")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Add a Product (ADMIN/VENDOR ONLY)", description = "Only Admins and Vendors can create products.")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDOR')") // Enforces either role
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        return new ResponseEntity<>(productService.createProduct(productDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update Inventory (VENDOR ONLY)", description = "Adds the specified quantity to the existing stock.")
    @PreAuthorize("hasRole('VENDOR')") // Enforces VENDOR only
    @PatchMapping("/{id}/inventory")
    public ResponseEntity<ProductDto> updateInventory(@PathVariable Long id, @RequestParam Integer quantityToAdd) {
        return ResponseEntity.ok(productService.updateInventory(id, quantityToAdd));
    }

    @Operation(summary = "Get Product by ID", description = "Publicly accessible.")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(summary = "Get All Products", description = "Publicly accessible.")
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
}