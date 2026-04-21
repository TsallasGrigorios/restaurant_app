package com.example.demo.dao;

import com.example.demo.model.Tables;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
public interface TablesRepository extends JpaRepository<Tables, Long> {
}