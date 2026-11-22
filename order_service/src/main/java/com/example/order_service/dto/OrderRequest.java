package com.example.order_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderRequest(
    @NotNull (message = "productId cannot be empty")
    Long productId,

    @NotNull(message = "quantity can't be blank")
    @Min(1)
    Integer quantity

){}
