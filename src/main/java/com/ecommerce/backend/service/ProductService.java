package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.ProductDto;
import com.ecommerce.backend.entity.Category;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository; // ADDED

    public ProductDto createProduct(ProductDto productDto) {
        // 1. Fetch the category to ensure it exists
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDto.getCategoryId()));

        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStockQuantity(productDto.getStockQuantity());
        product.setCategory(category); // Link category

        Product savedProduct = productRepository.save(product);
        productDto.setId(savedProduct.getId());
        return productDto;
    }

    // NEW METHOD: Update Inventory
    public ProductDto updateInventory(Long id, Integer quantityToAdd) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setStockQuantity(product.getStockQuantity() + quantityToAdd);
        Product updated = productRepository.save(product);

        // mapping back to DTO
        ProductDto dto = new ProductDto();
        dto.setId(updated.getId());
        dto.setName(updated.getName());
        dto.setDescription(updated.getDescription());
        dto.setPrice(updated.getPrice());
        dto.setStockQuantity(updated.getStockQuantity());
        return dto;
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToDto(product);
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Helper method
    private ProductDto mapToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setCategoryId(product.getCategory().getId());
        return dto;
    }
}