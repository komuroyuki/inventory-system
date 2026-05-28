package com.inventory.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory.Entity.Product;

public interface FilterRepository
        extends JpaRepository<Product, Integer> {

    List<Product> findByCategoryIdId(Integer categoryId);
    List<Product> findByCategoryIdIdAndNameContaining(Integer categoryId, String keyword);
}