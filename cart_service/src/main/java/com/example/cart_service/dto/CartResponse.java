package com.example.cart_service.dto;

import java.util.List;

public record CartResponse(
    Long id,
    String status,
    List<CartItemResponse> items
) {}
