package com.example.cart_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddCartItemRequest(

    @NotNull(message = "Product ID cannot be empty")
    @Positive(message = "Product ID must be greater than 0")
    Long productId,

    @NotNull(message = "Quantity cannot be empty")
    @Positive(message = "Quantity must be greater than 0")
    Integer quantity

) {}
