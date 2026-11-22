package com.example.order_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // applies to every controller, so all error handling lives in here
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class) // runs when valid fails in the controller

    // goes through each field that failed and puts it into a map with the field name and error message.
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // sends back a 400 bad request with the map above.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<Map<String, String>> handleOutOfStock(OutOfStockException ex) {
     return ResponseEntity.status(HttpStatus.CONFLICT)
         .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFound ex) {
     return ResponseEntity.status(HttpStatus.BAD_REQUEST)
         .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleBadRequestException(BadRequestException ex) {
     return ResponseEntity.status(HttpStatus.BAD_REQUEST)
         .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Map<String, String>> handleResourceAccessException(ResourceAccessException ex) {
     return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
         .body(Map.of("message", "Another service could not be reached please try again"));
    }
}