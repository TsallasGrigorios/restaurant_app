package com.example.demo.dao;




import com.example.demo.model.DailyOrders;
import com.example.demo.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyOrdersRepository extends JpaRepository<DailyOrders, Long> {
}