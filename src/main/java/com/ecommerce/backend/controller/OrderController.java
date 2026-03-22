package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.OrderRequestDto;
import com.ecommerce.backend.dto.OrderResponseDto;
import com.ecommerce.backend.dto.PaymentVerificationDto;
import com.ecommerce.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Checkout and payment verification APIs")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Place a new order", description = "Calculates total and returns a Razorpay Order ID.")
    @PostMapping
    public ResponseEntity<OrderResponseDto> placeOrder(@RequestBody OrderRequestDto orderRequest) {
        return new ResponseEntity<>(orderService.placeOrder(orderRequest), HttpStatus.CREATED);
    }

    @Operation(summary = "Get Order Details", description = "Fetch a specific order by ID.")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @Operation(summary = "Verify Razorpay Payment", description = "Cryptographically verifies payment success.")
    @PostMapping("/verify-payment")
    public ResponseEntity<OrderResponseDto> verifyPayment(@RequestBody PaymentVerificationDto verificationDto) {
        return ResponseEntity.ok(orderService.verifyPayment(verificationDto));
    }
}