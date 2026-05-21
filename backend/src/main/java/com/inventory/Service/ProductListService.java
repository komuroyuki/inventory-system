package com.inventory.Service;

import com.inventory.Repository.ProductRepository;
import com.inventory.Entity.Product;
import org.springframework.stereotype.Service;


@Service
public class ProductListService {

    private final ProductRepository productRepository;

    public ProductListService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Iterable<Product> getProductList() {
        return productRepository.findAll();
    }
}