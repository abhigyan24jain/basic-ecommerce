package com.ecommerce.backend.service;
import com.ecommerce.backend.dto.CategoryDto;
import com.ecommerce.backend.entity.Category;
import com.ecommerce.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryDto createCategory(CategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        Category saved = categoryRepository.save(category);
        dto.setId(saved.getId());
        return dto;
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream().map(cat -> {
            CategoryDto dto = new CategoryDto();
            dto.setId(cat.getId());
            dto.setName(cat.getName());
            return dto;
        }).collect(Collectors.toList());
    }
}