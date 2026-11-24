package com.example.inventory_service.exception;

// Out of stock exception 
// Runs if 
public class OutOfStockException extends RuntimeException {
    public OutOfStockException(String message) {
        super(message);
    }
}
