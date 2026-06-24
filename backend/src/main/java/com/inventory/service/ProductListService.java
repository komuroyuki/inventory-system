package com.inventory.service;

import com.inventory.repository.ProductRepository;
import com.inventory.entity.Product;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProductListService {

    private final ProductRepository productRepository;

    public ProductListService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProductList() {
        return productRepository.findAll();
    }
}
