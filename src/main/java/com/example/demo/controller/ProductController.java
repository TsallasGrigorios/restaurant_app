package com.example.demo.controller;

import com.example.demo.model.Products;
import com.example.demo.dao.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // Για να μπορεί να το καλεί το Flutter
public class ProductController {

    @Autowired
    private ProductsRepository repository;

    // Φέρνει όλο το μενού
    @GetMapping
    public List<Products> getAllProducts() {
        return repository.findAll();
    }

    // Προσθήκη νέου προϊόντος
    @PostMapping
    public Products addProduct(@RequestBody Products product) {
        return repository.save(product);
    }

    // Διαγραφή προϊόντος
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        repository.deleteById(id);
    }
    
    // Ενημέρωση προϊόντος
    @PutMapping("/{id}")
    public Products updateProduct(@PathVariable Long id, @RequestBody Products updatedProduct) {
        return repository.findById(id)
                .map(product -> {
                    product.setName(updatedProduct.getName());
                    product.setPrice(updatedProduct.getPrice());
                    product.setFpa(updatedProduct.getFpa());
                    product.setCategory(updatedProduct.getCategory());
                    return repository.save(product);
                })
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}