package com.example.productservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}