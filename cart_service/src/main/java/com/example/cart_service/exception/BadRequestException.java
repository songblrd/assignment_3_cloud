package com.example.cart_service.exception;

// becomes a 400: empty cart, cart already checked out, or order-service rejected the request
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
