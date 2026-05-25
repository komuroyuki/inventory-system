package com.inventory.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory.Entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByNameContaining(String keyword);
}
