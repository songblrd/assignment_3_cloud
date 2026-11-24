package com.example.cart_service.dto;

public record CartItemResponse(
    Long id,
    Long productId,
    Integer quantity
) {}
