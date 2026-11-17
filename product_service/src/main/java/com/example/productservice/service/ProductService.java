package com.example.productservice.service;

import com.example.productservice.model.Product;
import java.util.List;
import java.util.Optional;
import com.example.productservice.dto.ProductRequest;

// this is a list of what the service can do, but not how it does it
// the controller only uses this interface so that it doesnt use ProductServiceImpl directly. this ensures that you dont have to change the controller if you want to change something.
// which is loose coupling
public interface ProductService {
    List<Product> getAllProducts();

    Optional<Product> getProductById(Long id);

    List<Product> searchProducts(String category, String name);

    Product createProduct(ProductRequest request);

    Optional<Product> updateProduct(Long id, ProductRequest request);

    boolean deleteProduct(Long id);
}