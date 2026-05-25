package com.inventory.Service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.inventory.Entity.Product;
import com.inventory.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductRepository productRepository;

    public List<Product> searchProduct(String keyword) {
        return productRepository.findByNameContaining(keyword);
    }
}
