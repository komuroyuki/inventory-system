package com.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory.entity.Product;

public interface ProductSearchRepository
        extends JpaRepository<Product, Integer> {

    List<Product> findByCategoryIdId(Integer categoryId);

    List<Product> findByCategoryIdIdAndNameContaining(Integer categoryId, String keyword);
}
