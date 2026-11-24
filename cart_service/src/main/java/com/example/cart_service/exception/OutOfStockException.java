package com.example.cart_service.exception;

// becomes a 409: order-service (via inventory-service) said there is not enough stock
public class OutOfStockException extends RuntimeException {
    public OutOfStockException(String message) {
        super(message);
    }
}
