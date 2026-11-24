package com.example.cart_service.service;

import com.example.cart_service.client.OrderClient;
import com.example.cart_service.dto.AddCartItemRequest;
import com.example.cart_service.dto.OrderLineRequest;
import com.example.cart_service.dto.OrderResponse;
import com.example.cart_service.exception.BadRequestException;
import com.example.cart_service.model.Cart;
import com.example.cart_service.model.CartItem;
import com.example.cart_service.repository.CartRepository;
import com.example.cart_service.repository.CartItemRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//Service that implements cart service interface and logic
@Service 

public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderClient orderClient;

    // constructor injection: the repositories and the order-service client get passed in by Spring
    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, OrderClient orderClient) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderClient = orderClient;
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

            // if this product is already in the cart, add to that line instead of making a second one
            // (the schema allows one line per product per cart)
            for (CartItem item : existingCart.getItems()) {
                if (item.getProductId().equals(request.productId())) {
                    item.setQuantity(item.getQuantity() + request.quantity());
                    return cartRepository.save(existingCart);
                }
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

    @Override
    // sends every line in the cart to order-service; only marks the cart CHECKED_OUT if the orders were created
    // if the cart does not exist, returns an empty optional (404)
    public Optional<List<OrderResponse>> checkout(Long id) {
        return cartRepository.findById(id).map(cart -> {

            if ("CHECKED_OUT".equals(cart.getStatus())) {
                throw new BadRequestException("This cart has already been checked out.");
            }

            if (cart.getItems() == null || cart.getItems().isEmpty()) {
                throw new BadRequestException("Your cart is empty.");
            }

            // turn the cart lines into the {productId, quantity} list that order-service expects
            List<OrderLineRequest> lines = new ArrayList<>();
            for (CartItem item : cart.getItems()) {
                lines.add(new OrderLineRequest(item.getProductId(), item.getQuantity()));
            }

            // order-service checks products and reserves stock; a 409 or 400 throws here,
            // so the cart is NOT marked checked out when an order fails
            List<OrderResponse> orders = orderClient.createOrders(lines);

            cart.setStatus("CHECKED_OUT");
            cartRepository.save(cart);

            return orders;
        });
    }

}
