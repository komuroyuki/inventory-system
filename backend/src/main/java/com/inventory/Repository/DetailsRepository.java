package com.inventory.Repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory.Entity.Product;

public interface DetailsRepository extends JpaRepository<Product, Integer> {
    Product findFirstByIdGreaterThanOrderByIdAsc(Integer id);
        Product findFirstByIdLessThanOrderByIdDesc(Integer id);
}
