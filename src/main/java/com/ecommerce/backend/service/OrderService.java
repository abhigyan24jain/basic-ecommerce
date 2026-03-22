package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.OrderItemRequestDto;
import com.ecommerce.backend.dto.OrderRequestDto;
import com.ecommerce.backend.dto.OrderResponseDto;
import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.OrderItem;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ecommerce.backend.entity.OrderStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PaymentService paymentService;

    @Transactional
    public OrderResponseDto placeOrder(OrderRequestDto request) {
        // 1. Validate User
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Order order = new Order();
        order.setUser(user);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // 2. Process each item
        for (OrderItemRequestDto itemDto : request.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDto.getProductId()));

            // 3. Check stock
            if (product.getStockQuantity() < itemDto.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            // 4. Deduct stock
            product.setStockQuantity(product.getStockQuantity() - itemDto.getQuantity());
            productRepository.save(product);

            // 5. Create Order Item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPrice(product.getPrice()); // Record the price at time of purchase

            orderItems.add(orderItem);

            // 6. Add to total
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        // 7. Save Order (CascadeType.ALL will automatically save the OrderItems too)
        Order savedOrder = orderRepository.save(order);

        // 8. Generate Razorpay Payment ID
        String receiptId = "txn_" + savedOrder.getId();
        String razorpayOrderId = paymentService.createRazorpayOrder(totalAmount, receiptId);

        // 9. Map and Return Response
        OrderResponseDto responseDto = mapToResponseDto(savedOrder);
        responseDto.setRazorpayOrderId(razorpayOrderId); // Add the new ID to the response

        savedOrder.setRazorpayOrderId(razorpayOrderId);
        orderRepository.save(savedOrder); // Update the order with the ID

        return responseDto;

    }

    public OrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapToResponseDto(order);
    }

    private OrderResponseDto mapToResponseDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus().name());
        dto.setOrderDate(order.getOrderDate());
        return dto;
    }
    @Transactional
    public String verifyAndConfirmOrder(com.ecommerce.backend.dto.PaymentVerificationDto request) {
        // 1. Verify the signature securely
        boolean isValid = paymentService.verifyPaymentSignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature()
        );

        if (!isValid) {
            throw new RuntimeException("Payment signature verification failed!");
        }

        // 2. Find the order by its Razorpay Order ID (Wait, we need to save this in the DB first!
        return "Payment Successful! Order is confirmed.";
    }
    @Transactional
    public OrderResponseDto verifyPayment(com.ecommerce.backend.dto.PaymentVerificationDto request) {
        boolean isValid = paymentService.verifyPaymentSignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature()
        );

        if (!isValid) {
            throw new RuntimeException("Invalid payment signature");
        }

        // Find the order using the Razorpay ID
        Order order = orderRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Update status
        order.setStatus(com.ecommerce.backend.entity.OrderStatus.PAID);
        Order updatedOrder = orderRepository.save(order);

        return mapToResponseDto(updatedOrder);
    }
    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderId, com.ecommerce.backend.entity.OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        // Prevent modifying already cancelled orders
        if (order.getStatus() == com.ecommerce.backend.entity.OrderStatus.CANCELLED) {
            throw new RuntimeException("Order is already cancelled and cannot be modified.");
        }

        // BUSINESS LOGIC: If the order is being cancelled, we MUST restock the inventory
        if (newStatus == com.ecommerce.backend.entity.OrderStatus.CANCELLED) {
            for (com.ecommerce.backend.entity.OrderItem item : order.getOrderItems()) {
                com.ecommerce.backend.entity.Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productRepository.save(product); // Put the stock back!
            }
        }

        // Update the status and save
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        return mapToResponseDto(updatedOrder);
    }
}