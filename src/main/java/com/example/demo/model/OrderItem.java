package com.example.demo.model;


import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Orders order;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Products product; // Σύνδεση με το προϊόν από το μενού
    
    @Column(name = "comment")
    private String comment;
    @Column(name = "sugar")
    private String sugar;
    private int quantity;
    
    private double priceAtTimeOfOrder; // Αποθήκευση τιμής για ιστορικότητα

    public OrderItem() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Products getProduct() { return product; }
    public void setProduct(Products product) {
        this.product = product;
    }
    public Orders getOrder() { 
        return order; 
    }

    public void setOrder(Orders order) { 
        this.order = order; 
    }
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getSugar() {
        return sugar;
    }

    public void setSugar(String sugar) {
        this.sugar = sugar;
    }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPriceAtTimeOfOrder() { return priceAtTimeOfOrder; }
    public void setPriceAtTimeOfOrder(double priceAtTimeOfOrder) { 
        this.priceAtTimeOfOrder = priceAtTimeOfOrder; 
    }
}