package com.example.productservice.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity // marks this as a jpa entity mapped to database table
@Table(name = "products") // this sets the table in postgresql with the set name.
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // this is the primary key, is never set by anyone just postgresql
    private Long id;

    @Column(nullable = false)

    private String name;

    @Column(nullable = false) // nullable false sets it as not being able to be null. basically says this cant be null.
    private String category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stockQuantity;

    public Product() {

    } // jpa needs an empty constructor when it reads rows from the database

    public Product(String name, String category, BigDecimal price, Integer stockQuantity) {
        this.name = name;
        this.category =  category;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // getters and setters needed by jpa and by jackson to convert to json

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

}