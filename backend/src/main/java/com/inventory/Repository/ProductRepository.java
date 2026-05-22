package com.inventory.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory.Entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
