package com.example.inventory_service.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.inventory_service.dto.InventoryRequest;
import com.example.inventory_service.dto.ReserveRequest;
import com.example.inventory_service.exception.OutOfStockException;
import com.example.inventory_service.model.Inventory;
import com.example.inventory_service.repository.InventoryRepository;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<Inventory> getAll() {
        return inventoryRepository.findAll();
    }

    public Optional<Inventory> getById(Long id) {
        return inventoryRepository.findById(id);
    }

    public Optional<Inventory> getByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId);
    }

    public Inventory create(InventoryRequest request) {
        if (inventoryRepository.findByProductId(request.productId()).isPresent()) {
            throw new IllegalArgumentException("Inventory already exists for product " + request.productId());
        }
        return inventoryRepository.save(new Inventory(request.productId(), request.available()));
    }

    public Optional<Inventory> update(Long id, InventoryRequest request) {
        if (inventoryRepository.findByProductId(request.productId())
            .filter(existing -> !existing.getId().equals(id))
            .isPresent()) {
            throw new IllegalArgumentException("Inventory already exists for product " + request.productId());
        }
        return inventoryRepository.findById(id).map(existing -> {
            existing.setProductId(request.productId());
            existing.setAvailable(request.available());
            return inventoryRepository.save(existing);
        });
    }

    public boolean delete(Long id) {
        if (!inventoryRepository.existsById(id)) {
            return false;
        }
        inventoryRepository.deleteById(id);
        return true;
    }

    @Transactional
    public void reserve(List<ReserveRequest> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("Reservation lines cannot be empty");
        }

        Map<Long, Integer> totals = new HashMap<>();
        for (ReserveRequest line : lines) {
            totals.merge(line.productId(), line.quantity(), Integer::sum);
        }

        for (Map.Entry<Long, Integer> entry : totals.entrySet()) {
            Inventory inventory = inventoryRepository.findByProductId(entry.getKey())
                .orElseThrow(() -> new OutOfStockException("One of your items is out of stock"));
            if (inventory.getAvailable() < entry.getValue()) {
                throw new OutOfStockException("One of your items is out of stock");
            }
        }

        for (Map.Entry<Long, Integer> entry : totals.entrySet()) {
            Inventory inventory = inventoryRepository.findByProductId(entry.getKey()).orElseThrow();
            inventory.setAvailable(inventory.getAvailable() - entry.getValue());
            inventoryRepository.save(inventory);
        }
    }
}
