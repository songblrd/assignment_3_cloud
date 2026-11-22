package com.example.order_service.service;

import org.springframework.stereotype.Service;

import com.example.order_service.client.InventoryClient;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.client.ProductClient;
import com.example.order_service.dto.OrderRequest;
import com.example.order_service.dto.ProductResponse;
import com.example.order_service.model.Order;
import com.example.order_service.exception.BadRequestException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service 
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;

    public OrderService(OrderRepository orderRepository, ProductClient productClient, InventoryClient inventoryClient){
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.inventoryClient = inventoryClient;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> createOrder(List<OrderRequest> lines){
        if (lines == null || lines.isEmpty()){
            throw new BadRequestException("Cart is empty");
        }
        List<BigDecimal> prices = new ArrayList<>();
        for (OrderRequest line: lines){
            ProductResponse product = productClient.getProduct(line.productId());
            prices.add(product.price());
            
        }

        inventoryClient.reserve(lines);

        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++){
            OrderRequest line = lines.get(i);
            BigDecimal price = prices.get(i);
            orders.add(new Order(
                line.productId(),
                line.quantity(),
                price.multiply(BigDecimal.valueOf(line.quantity())),
                "CONFIRMED"
            ));
        }
        return orderRepository.saveAll(orders);
    }
}
