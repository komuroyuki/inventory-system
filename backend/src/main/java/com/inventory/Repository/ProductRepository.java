package com.inventory.Repository;

import org.springframework.data.repository.CrudRepository;

import com.inventory.Entity.Product;

public interface ProductRepository extends CrudRepository<Product, String> {

}
