package com.example.demo.controller;

import com.example.demo.model.Orders;
import com.example.demo.model.Tables;

import jakarta.transaction.Transactional;

import com.example.demo.model.DailyOrders;
import com.example.demo.model.OrderItem;
import com.example.demo.dao.DailyOrdersRepository;
import com.example.demo.dao.OrdersRepository;
import com.example.demo.dao.ProductsRepository;
import com.example.demo.dao.TablesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class DailyOrderController {

    @Autowired
    private OrdersRepository orderRepository;

    @Autowired
    private DailyOrdersRepository dailyOrdersRepository;

    @Autowired
    private TablesRepository tableRepository;

    @Transactional
    @PostMapping("/{id}/close")
    public ResponseEntity<?> closeAndArchiveOrder(@PathVariable Long id) {
        
        // 1. Έλεγχος αν υπάρχει η παραγγελία
        Optional<Orders> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Η παραγγελία δεν βρέθηκε");
        }
        Orders activeOrder = orderOpt.get();

        // --- ΥΠΟΛΟΓΙΣΜΟΣ ΦΠΑ ---
        double totalFpaAmount = 0.0;

        for (OrderItem item : activeOrder.getItems()) {
            double itemPrice = item.getPriceAtTimeOfOrder();
            int quantity = item.getQuantity();
            double fpaRate = item.getProduct().getFpa(); // Παίρνει 0.13 ή 0.24

            // Υπολογισμός: Αν η τιμή περιλαμβάνει ΦΠΑ (π.χ. 2.00€ συνολικά)
            // Ο τύπος είναι: Συνολική Τιμή - (Συνολική Τιμή / (1 + fpaRate))
            double itemTotal = itemPrice * quantity *fpaRate;
            //double fpaForThisItem = itemTotal - (itemTotal / (1 + fpaRate));
            
            totalFpaAmount += itemTotal;
        }

        // 2. Μεταφορά στο DailyOrders
        DailyOrders daily = new DailyOrders();
        daily.setTable(activeOrder.getTable());
        daily.setTotalPrice(activeOrder.getTotalPrice());
        
        // Προσθήκη του ΦΠΑ στο DailyOrder (αν έχεις φτιάξει το πεδίο setTotalFpa)
        // daily.setTotalFpa(totalFpaAmount);

        // Εδώ ελέγχουμε αν το color είναι διάφορο του 0 για το "Black"
        daily.setBlack(activeOrder.getTable().getColor());
        daily.setStatus("PAID");
        daily.setFpa(totalFpaAmount);
        dailyOrdersRepository.save(daily);

        // 3. Καθαρισμός Τραπεζιού
        Tables table = activeOrder.getTable();
        table.setStatus("free");
        table.setColor(0);
        table.setCapacity(0);
        tableRepository.save(table);

        // 4. Διαγραφή
        orderRepository.delete(activeOrder);

        return ResponseEntity.ok("Η παραγγελία ολοκληρώθηκε. ΦΠΑ: " + String.format("%.2f", totalFpaAmount));
    }
 // Endpoint για να παίρνουμε όλες τις κλεισμένες παραγγελίες της ημέρας
    @GetMapping("/daily")
    public ResponseEntity<List<DailyOrders>> getAllDailyOrders() {
        List<DailyOrders> dailyOrders = dailyOrdersRepository.findAll();
        return ResponseEntity.ok(dailyOrders);
    }


    @DeleteMapping("/daily/{id}")
    public ResponseEntity<?> deleteDailyOrder(@PathVariable Long id) {
        try {
            // Έλεγχος αν υπάρχει
            if (!dailyOrdersRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            // Διαγραφή
            dailyOrdersRepository.deleteById(id);
            return ResponseEntity.ok("Η παραγγελία διαγράφηκε επιτυχώς");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Σφάλμα κατά τη διαγραφή");
        }
    }
}