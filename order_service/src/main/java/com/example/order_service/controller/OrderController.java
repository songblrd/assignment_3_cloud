package com.example.order_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.order_service.dto.OrderRequest;
import com.example.order_service.model.Order;
import java.util.List;
import jakarta.validation.Valid;

import com.example.order_service.service.OrderService;

@RestController 
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService){
       this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getOrders(){
        return orderService.getAllOrders();
    }

    @PostMapping
    public ResponseEntity<List<Order>> createOrder(@Valid @RequestBody List<OrderRequest> lines) {
        List<Order> saved = orderService.createOrder(lines);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


}
