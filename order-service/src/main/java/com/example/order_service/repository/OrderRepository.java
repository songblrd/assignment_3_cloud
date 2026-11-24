package com.example.order_service.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.order_service.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Define custom repository methods here
}
