package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Products {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private Double price;
    private Double fpa;
    private String category;

    // Default Constructor (Απαραίτητος για το JPA)
    public Products() {}

    // Constructor με όλα τα πεδία
    public Products(Long id, String name, Double price, Double fpa, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.fpa = fpa;
        this.category = category;
    }

    // --- Getters and Setters ---

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getFpa() {
        return fpa;
    }

    public void setFpa(Double fpa) {
        this.fpa = fpa;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}