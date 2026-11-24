package com.example.cart_service.service;

import com.example.cart_service.dto.AddCartItemRequest;
import com.example.cart_service.dto.OrderResponse;
import com.example.cart_service.model.Cart;

import java.util.List;
import java.util.Optional;

// this is a service interface for cart operations and defines the methods that will be implemented in the service class.
public interface CartService {

    Cart createCart();

    Optional<Cart> getCartById(Long id);

    Optional<Cart> addItem(Long id, AddCartItemRequest request);

    boolean removeItem(Long id, Long itemId);

    Optional<List<OrderResponse>> checkout(Long id);
}