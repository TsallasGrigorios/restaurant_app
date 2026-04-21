package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_orders")
public class DailyOrders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // We store the table reference (or just the table ID/number)
    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private Tables table;

    private String status; 
    private Integer isBlack;
    private double totalPrice; // This will be copied from the active order before it's deleted
    private double fpa;
    private LocalDateTime closedAt; // Good practice to know WHEN it was moved to daily

    public DailyOrders() {
        this.closedAt = LocalDateTime.now();
    }

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

    public double getFpa() { return fpa; }
    public void setFpa(double fpa) { this.fpa = fpa; }
    
    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }
}