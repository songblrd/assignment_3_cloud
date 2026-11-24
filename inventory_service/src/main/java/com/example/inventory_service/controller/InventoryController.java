package com.example.inventory_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.inventory_service.dto.InventoryRequest;
import com.example.inventory_service.dto.ReserveRequest;
import com.example.inventory_service.model.Inventory;
import com.example.inventory_service.service.InventoryService;

import jakarta.validation.Valid;

// Inventory rest controller
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // Get all
    @GetMapping
    public List<Inventory> getAll() {
        return inventoryService.getAll();
    }

    // Get by id
    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getById(@PathVariable Long id) {
        return inventoryService.getById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // Get by product id
    @GetMapping("/product/{productId}")
    public ResponseEntity<Inventory> getByProductId(@PathVariable Long productId) {
        return inventoryService.getByProductId(productId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // Create product
    @PostMapping
    public ResponseEntity<Inventory> create(@Valid @RequestBody InventoryRequest request) {
        Inventory saved = inventoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Update product
    @PutMapping("/{id}")
    public ResponseEntity<Inventory> update(@PathVariable Long id, @Valid @RequestBody InventoryRequest request) {
        return inventoryService.update(id, request)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // Delete product by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return inventoryService.delete(id)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.notFound().build();
    }

    // 
    @PostMapping("/reserve")
    public ResponseEntity<Void> reserve(@Valid @RequestBody List<ReserveRequest> lines) {
        inventoryService.reserve(lines);
        return ResponseEntity.ok().build();
    }
}
