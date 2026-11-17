package com.example.productservice.controller;

import com.example.productservice.model.Product;
import com.example.productservice.dto.ProductRequest;
import com.example.productservice.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import java.util.List;

// this file is the entry point for the http request

@RestController // marks the class as a controller and returns values as json
@RequestMapping("/api/products") // this is the base url, or how we access the api
public class ProductController {

    private final ProductService productService;

    @Value("${warehouse.name}") // this gets the value from application.yaml, and depends on what profile is active
    private String warehouseName;

    @Value("${warehouse.location}")
    private String location;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping("/info") // this shows the injected config values
    public String getProductInfo() {
        return "Welcome to " + warehouseName + " (" + location + ")";
    }

    @GetMapping // this ensures that we return all the products
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}") // id is the placeholder, and pathvariable copies it into the id parameter. will return 200 with it, if not returns 404 not found
    public ResponseEntity<Product> getProductbyId(@PathVariable Long id) {
        return productService.getProductById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping // this is for creating a new product, basically telling that we are making a new one. valid ensures validation runs on the dto (data transfer object)
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request){
        Product savedProduct = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct); // makes sure the api returns successful create code (201)
    }
    
    @GetMapping("/search") 
    public List<Product> searchProducts(
        // request parameter is basically saying that category and name are what we are looking to search. required false makes it optional.
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String name) {
            return productService.searchProducts(category, name);
        }

    @PutMapping("/{id}") // this is for updating a product, basically telling that we are changing an existing one. valid ensures validation runs on the dto (data transfer object)
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}") // this is for deleting a product, basically telling that we are removing an existing one.
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.noContent().build(); // returns 204 no content if deleted
        } else {
            return ResponseEntity.notFound().build(); // returns 404 not found if not found
        }
    }
    
}