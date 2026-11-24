package com.example.inventory_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record InventoryRequest(
    @NotNull(message = "Product id cannot be empty")
    @Positive(message = "Product id must be greater than 0")
    Long productId,

    @NotNull(message = "Available quantity cannot be empty")
    @PositiveOrZero(message = "Available quantity cannot be negative")
    Integer available
) {}
