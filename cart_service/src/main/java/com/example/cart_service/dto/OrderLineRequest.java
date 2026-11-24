package com.example.cart_service.dto;

// one cart line sent to order-service: POST /api/orders takes a list of these
public record OrderLineRequest(
    Long productId,
    Integer quantity
) {}
