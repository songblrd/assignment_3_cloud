package com.example.productservice.repository;

import com.example.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository // this marks the interface as a data access layer, and this is where Dependency Injection comes in, as it injects it into the service.

// extends here inherits the built in methods from the extended interface. Not a lot of code here because Spring generates implementation at runtime.
// it manages product and long is the type of its id.
public interface ProductRepository extends JpaRepository<Product, Long>{
    // this is to find the product by name or parts of the name, ignores case so if you wanted to find "strawberry" it will find "Strawberry"
    List<Product> findByNameContainingIgnoreCase(String name);

    // this is to find the product by category, also ignoring case like the find by name one. Spring reads the method name and writes the database query for me.
    List<Product> findByCategoryIgnoreCase(String category);
}