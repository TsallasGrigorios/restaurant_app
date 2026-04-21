package com.example.demo.model;

import jakarta.persistence.*;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;

@Entity
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private Tables table; // Η οντότητα του τραπεζιού σου

    private String status; // π.χ. "OPEN", "PAID", "CANCELLED"
    
    private int isBlack; // true = χωρίς απόδειξη, false = κανονική
    
    private double totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<OrderItem> items = new ArrayList<>();

    public Orders() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tables getTable() { return table; }
    public void setTable(Tables table) { this.table = table; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getBlack() { return isBlack; }
    public void setBlack(Integer black) { isBlack = black; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { 
        this.items = items; 
        updateTotalPrice(); // Ενημέρωση συνόλου όταν αλλάζει η λίστα
    }

    // Βοηθητική μέθοδος για υπολογισμό συνόλου
    public void updateTotalPrice() {
        this.totalPrice = items.stream()
                .mapToDouble(item -> item.getPriceAtTimeOfOrder() * item.getQuantity())
                .sum();
    }
}