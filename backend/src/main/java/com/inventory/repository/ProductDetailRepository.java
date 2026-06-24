package com.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory.entity.Product;

public interface ProductDetailRepository extends JpaRepository<Product, Integer> {

    Product findFirstByIdGreaterThanOrderByIdAsc(Integer id);

    Product findFirstByIdLessThanOrderByIdDesc(Integer id);
}
