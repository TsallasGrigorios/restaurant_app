package com.example.demo.controller;

import com.example.demo.model.Orders;
import com.example.demo.model.OrderItem;
import com.example.demo.dao.OrdersRepository;
import com.example.demo.dao.ProductsRepository;
import com.example.demo.dao.TablesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrdersRepository orderRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private TablesRepository tableRepository;

 // ... (οι υπόλοιπες εισαγωγές παραμένουν ίδιες)

    @PostMapping
    public ResponseEntity<?> saveOrder(@RequestBody Orders order) { // Αλλάξαμε σε <?> για να επιτρέπεται επιστροφή String σε περίπτωση λάθους
        try {
            // 🔥 ΕΛΕΓΧΟΣ: Αν η παραγγελία δεν έχει προϊόντα, σταμάτα τη διαδικασία
            if (order.getItems() == null || order.getItems().isEmpty()) {
                return ResponseEntity.badRequest().body("Δεν μπορείτε να αποθηκεύσετε μια κενή παραγγελία.");
            }

            Orders existingOrder;
            if (order.getId() != null) {
                existingOrder = orderRepository.findById(order.getId())
                        .orElseThrow(() -> new RuntimeException("Order not found"));
            } else {
                existingOrder = new Orders();
                // Βρίσκουμε το τραπέζι από τη βάση
                var table = tableRepository.findById(order.getTable().getId())
                        .orElseThrow(() -> new RuntimeException("Table not found"));
                
                // Κάνουμε το τραπέζι occupied
                table.setStatus("OCCUPIED");
                tableRepository.save(table); 

                existingOrder.setTable(table);
                existingOrder.setStatus("OPEN");
                existingOrder.setBlack(order.getBlack());
            }

            // Επεξεργασία των items... (ο υπόλοιπος κώδικας παραμένει ίδιος)
            for (OrderItem incomingItem : order.getItems()) {
                var product = productsRepository.findById(incomingItem.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                OrderItem existingItem = existingOrder.getItems().stream()
                        .filter(i -> {
                            boolean sameProd = i.getProduct().getId().equals(product.getId());
                            boolean sameSugar = Objects.equals(i.getSugar(), incomingItem.getSugar());
                            String c1 = (i.getComment() == null) ? "" : i.getComment().trim();
                            String c2 = (incomingItem.getComment() == null) ? "" : incomingItem.getComment().trim();
                            return sameProd && c1.equals(c2) && sameSugar;
                        })
                        .findFirst().orElse(null);

                if (existingItem != null) {
                    existingItem.setQuantity(existingItem.getQuantity() + incomingItem.getQuantity());
                    existingItem.setSugar(incomingItem.getSugar()); 
                } else {
                    OrderItem newItem = new OrderItem();
                    newItem.setProduct(product);
                    newItem.setQuantity(incomingItem.getQuantity());
                    newItem.setPriceAtTimeOfOrder(product.getPrice());
                    newItem.setComment(incomingItem.getComment());
                    newItem.setSugar(incomingItem.getSugar()); 
                    newItem.setOrder(existingOrder);
                    existingOrder.getItems().add(newItem);
                }
            }

            existingOrder.updateTotalPrice();
            return ResponseEntity.ok(orderRepository.save(existingOrder));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/table/{tableId}/open")
    public ResponseEntity<Orders> getOpenOrder(@PathVariable Long tableId) {
        List<Orders> openOrders = orderRepository.findAll().stream()
                .filter(o -> o.getTable().getId().equals(tableId) && "OPEN".equals(o.getStatus()))
                .toList();

        if (openOrders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Φτιάχνουμε μια "συνολική" παραγγελία για το Flutter
        Orders combinedOrder = new Orders();
        combinedOrder.setTable(openOrders.get(0).getTable());
        combinedOrder.setStatus("OPEN");
        combinedOrder.setBlack(openOrders.get(0).getBlack());
        
        // Μαζεύουμε όλα τα items από όλες τις παραγγελίες
        List<OrderItem> allItems = new ArrayList<>();
        double totalCombinedPrice = 0;

        for (Orders o : openOrders) {
            allItems.addAll(o.getItems());
            totalCombinedPrice += o.getTotalPrice();
        }

        combinedOrder.setItems(allItems);
        combinedOrder.setTotalPrice(totalCombinedPrice);
        // Χρησιμοποιούμε το ID της πρώτης παραγγελίας για να ξέρουμε πού να προσθέσουμε τα επόμενα
        combinedOrder.setId(openOrders.get(0).getId()); 

        return ResponseEntity.ok(combinedOrder);
    }
    @PutMapping("/{orderId}/items/remove")
    public ResponseEntity<?> removeOneItem(@PathVariable Long orderId, 
                                                @RequestParam Long productId, 
                                                @RequestParam String comment) {
        try {
            Orders order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            OrderItem itemToUpdate = order.getItems().stream()
                    .filter(i -> i.getProduct().getId().equals(productId) && 
                            ((i.getComment() == null ? "" : i.getComment().trim())
                            .equals(comment.trim())))
                    .findFirst().orElse(null);

            if (itemToUpdate != null) {
                if (itemToUpdate.getQuantity() > 1) {
                    itemToUpdate.setQuantity(itemToUpdate.getQuantity() - 1);
                } else {
                    order.getItems().remove(itemToUpdate);
                }

                // ΕΛΕΓΧΟΣ: Αν η παραγγελία έμεινε άδεια
                if (order.getItems().isEmpty()) {
                    var table = order.getTable();
                    
                    // 1. Ενημερώνουμε την κατάσταση του τραπεζιού στη βάση
                    table.setCapacity(0);
                    table.setStatus("free"); // ή table.setOccupied(false); ανάλογα το μοντέλο σου
                    tableRepository.save(table);
                    
                    // 2. Διαγράφουμε την κενή παραγγελία
                    orderRepository.delete(order);
                    
                    return ResponseEntity.ok("Table is now FREE");
                } else {
                    order.updateTotalPrice();
                    return ResponseEntity.ok(orderRepository.save(order));
                }
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}