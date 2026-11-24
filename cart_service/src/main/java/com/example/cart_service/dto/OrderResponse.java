package com.example.cart_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

// cart-service's copy of the order JSON that order-service sends back (services share JSON, not classes)
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderResponse(
    Long id,
    Long productId,
    Integer quantity,
    BigDecimal totalPrice,
    String status
) {}
