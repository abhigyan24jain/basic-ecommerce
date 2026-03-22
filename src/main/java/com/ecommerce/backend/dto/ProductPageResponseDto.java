package com.ecommerce.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductPageResponseDto {
    private List<ProductDto> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}