package com.example.productservice.service;

import com.example.productservice.dto.ProductRequest;
import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // registers with spring so it can be injected into the controller

// this provides the HOW for the Product service listed in the interface.
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

   // constructor injection: the repository gets passed in by Spring
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override // marks the method as "comes from the interface". if names dont match java gives an error   
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id){
        return productRepository.findById(id);
    }

    @Override
    public List<Product> searchProducts(String category, String name){
        // this is if category was given, checks if not null and is not blank in the category field
        if (category != null && !category.isBlank()){
            return productRepository.findByCategoryIgnoreCase(category);
        }
        // this is to check the name, the same way as category
        if (name != null && !name.isBlank()){
            return productRepository.findByNameContainingIgnoreCase(name);
        }
        // neither was given so return all
        return productRepository.findAll();
    }
 
    @Override
    // converts the Data Transfer Object into a Product entity, then inserts it into the database and returns it with its new generated id
    public Product createProduct(ProductRequest request){
        Product newProduct = new Product(request.name(), request.category(), request.price(), request.stockQuantity());
        return productRepository.save(newProduct);
    }

    @Override
    // updates the product with the given id, if it exists. if it does not exist, returns an empty optional
    public Optional<Product> updateProduct(Long id, ProductRequest request) {
        return productRepository.findById(id).map(existingProduct -> {
            existingProduct.setName(request.name());
            existingProduct.setCategory(request.category());
            existingProduct.setPrice(request.price());
            existingProduct.setStockQuantity(request.stockQuantity());
            return productRepository.save(existingProduct);
        });
    }


    @Override
    // deletes the product with the given id, if it exists. if it does not exist, returns false
    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }


}