package com.example.cart_service.controller;

import com.example.cart_service.model.Cart;
import com.example.cart_service.dto.AddCartItemRequest;
import com.example.cart_service.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

// Entry point for http requests

@RestController 
@RequestMapping("/api/carts") 
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // this creates a new cart and returns it with a 201 created status
    @PostMapping 
    public ResponseEntity<Cart> createCart() {
        Cart savedCart = cartService.createCart();
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCart); // returns 201 created
    }

    // this gets a cart by id, if it does not exist, returns 404 not found
    @GetMapping("/{id}") 
    public ResponseEntity<Cart> getCartById(@PathVariable Long id) {
        return cartService.getCartById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // this adds a product to an existing cart
    @PostMapping("/{id}/items") 
    public ResponseEntity<Cart> addItem(
            @PathVariable Long id,
            @RequestBody AddCartItemRequest request) {

        return cartService.addItem(id, request)
            .map(cart -> ResponseEntity.status(HttpStatus.CREATED).body(cart))
            .orElse(ResponseEntity.notFound().build());
    }

    // this removes an item from an existing cart
    @DeleteMapping("/{id}/items/{itemId}") 
    public ResponseEntity<Void> removeItem(
        @PathVariable Long id,
        @PathVariable Long itemId) {
            boolean deleted = cartService.removeItem(id, itemId);

            if (deleted) {
                return ResponseEntity.noContent().build(); // returns 204 no content if deleted
            } else {
                return ResponseEntity.notFound().build(); // returns 404 not found if not found
            }
    }

    // this starts checkout for all items in the cart
    @PostMapping("/{id}/checkout") // this starts checkout for all items in the cart
    public ResponseEntity<Cart> checkout(@PathVariable Long id) {
        return cartService.checkout(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

}
