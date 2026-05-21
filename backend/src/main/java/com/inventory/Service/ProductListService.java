package com.inventory.Service;

import com.inventory.Repository.ProductRepository;
import com.inventory.Entity.Product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProductListService {

    private final ProductRepository productRepository;

    public ProductListService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProductList() {

        Iterable<Product> iterable = productRepository.findAll();
        List<Product> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
}
