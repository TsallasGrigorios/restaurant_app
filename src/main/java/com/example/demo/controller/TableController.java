package com.example.demo.controller;

import com.example.demo.model.Tables;
import com.example.demo.dao.TablesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@CrossOrigin(origins = "*")
public class TableController {

    @Autowired
    private TablesRepository repository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Φέρνει όλα τα τραπέζια (για όταν ανοίγει η εφαρμογή)
    @GetMapping
    public List<Tables> getAllTables() {
        return repository.findAll();
    }

    // Αλλάζει το status (π.χ. από FREE σε OCCUPIED)
    @GetMapping("/{id}/status")
    public Tables updateTableStatus(@PathVariable Long id, @RequestParam String newStatus) {
        Tables table = repository.findById(id).orElseThrow();
        table.setStatus(newStatus);
        
        Tables updatedTable = repository.save(table);

        // Στέλνουμε την αλλαγή σε όλους στο κανάλι /topic/tables
        messagingTemplate.convertAndSend("/topic/tables", updatedTable);

        return updatedTable;
    }
    @GetMapping("/{id}/people")
    public Tables updateTablePeople(@PathVariable Long id, @RequestParam int peopleCount) {
        Tables table = repository.findById(id).orElseThrow();
        table.setCapacity(peopleCount); // Βεβαιώσου ότι έχεις αυτό το πεδίο στο Model σου
        
        Tables updatedTable = repository.save(table);
        messagingTemplate.convertAndSend("/topic/tables", updatedTable);
        return updatedTable;
    }
    @GetMapping("/{id}/toggle-color")
    public Tables toggleTableColor(@PathVariable Long id) {
        Tables table = repository.findById(id).orElseThrow(() -> new RuntimeException("Table not found"));
        
        // Αλλαγή: αν είναι 1 γίνεται 0, αν είναι 0 γίνεται 1
        int newColor = (table.getColor() == 1) ? 0 : 1;
        table.setColor(newColor);
        
        Tables updatedTable = repository.save(table);

        // Στέλνουμε την αλλαγή μέσω WebSocket
        messagingTemplate.convertAndSend("/topic/tables", updatedTable);

        return updatedTable;
    }
}