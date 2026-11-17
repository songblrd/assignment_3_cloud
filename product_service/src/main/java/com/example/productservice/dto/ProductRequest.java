package com.example.productservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// this is a dto, Data Transfer Object, defines what a client can send when creating a product. Keeps the api separate from the entity, so client cant set the id
// and ensuring decoupling and encapsulation.
// record is immutable, and java generates the constructor and accessors.
public record ProductRequest(
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100, message = "Name cannot exceed 100 characters") // makes it so that the size of the name input doesnt exceed 100 characters.
    String name,

    @NotBlank(message = "Category cannot be blank") // ensures that category isnt blank
    @Size(max = 100, message = "Category name cannot exceed 100 characters")
    String category,

    @NotNull(message = "Price cannot be empty") // ensures that it isnt null
    @Positive(message = "Price must be greater than 0")
    Double price,

    @NotNull(message = "Stock Quantity cannot be empty")
    @Positive(message = "Stock quantity cannot be zero") // this ensures that 0 on this field is not allowed, as it will take a missing value as valid
    Integer stockQuantity
    // this is integer instead of int because wrapper types can be null, so both not null and positive can catch a missing value. int would not.
) {}
