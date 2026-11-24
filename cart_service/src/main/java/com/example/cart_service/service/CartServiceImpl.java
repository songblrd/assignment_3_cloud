package com.example.cart_service.service;

import com.example.cart_service.dto.AddCartItemRequest;
import com.example.cart_service.model.Cart;
import com.example.cart_service.model.CartItem;
import com.example.cart_service.repository.CartRepository;
import com.example.cart_service.repository.CartItemRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

//Service that implements cart service interface and logic
@Service 

public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    // constructor injection: the repositories get passed in by Spring
    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    // creates a new empty cart with OPEN status and saves it to the database
    public Cart createCart() {
        Cart newCart = new Cart("OPEN");
        newCart.setItems(new ArrayList<>());
        return cartRepository.save(newCart);
    }

    @Override
    // finds the cart with the given id, if it does not exist, returns an empty optional
    public Optional<Cart> getCartById(Long id) {
        return cartRepository.findById(id);
    }

    @Override
    // adds a product to the cart, if the cart does not exist, returns an empty optional
    public Optional<Cart> addItem(Long id, AddCartItemRequest request) {
        return cartRepository.findById(id).map(existingCart -> {

            if (existingCart.getItems() == null) {
                existingCart.setItems(new ArrayList<>());
            }

            CartItem newItem = new CartItem(
                existingCart,
                request.productId(),
                request.quantity()
            );

            existingCart.getItems().add(newItem);

            return cartRepository.save(existingCart);
        });
    }

    @Override
    // removes an item from the cart, if it does not exist, returns false
    public boolean removeItem(Long id, Long itemId) {

        Optional<Cart> optionalCart = cartRepository.findById(id);

        if (optionalCart.isEmpty()) {
            return false;
        }

        Cart cart = optionalCart.get();

        Optional<CartItem> optionalItem = cartItemRepository.findById(itemId);

        if (optionalItem.isEmpty() || !optionalItem.get().getCart().getId().equals(id)) {
            return false;
        }

        cart.getItems().remove(optionalItem.get());
        cartRepository.save(cart);

        return true;
    }

    ///NOTE checkout will be added when connecting to order-service. This is a placeholder
    @Override
    public Optional<Cart> checkout(Long id) {
        return cartRepository.findById(id).map(cart -> {
            cart.setStatus("CHECKED_OUT");
            return cartRepository.save(cart);
        });
    }

}
