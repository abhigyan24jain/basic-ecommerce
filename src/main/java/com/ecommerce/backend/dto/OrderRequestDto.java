package com.ecommerce.backend.dto;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequestDto {
    private Long userId;
    private List<OrderItemRequestDto> items;
}