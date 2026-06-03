package com.inventory.Service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.inventory.DTO.ProductRequest;
import com.inventory.Entity.Category;
import com.inventory.Entity.Product;
import com.inventory.Repository.CategoryRepository;
import com.inventory.Repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductPostService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ResponseEntity<?> postProduct(ProductRequest request) {

        Category category = categoryRepository.findById(request.categoryId()).orElse(null);

        if (category == null) {
            return ResponseEntity.badRequest().body("カテゴリIDが存在しません。" + System.lineSeparator());
        }

        Product product = new Product();

        product.setName(request.name());
        product.setQuantity(request.quantity());
        product.setImage(request.image());
        product.setCategoryId(category);

        Product newProduct = productRepository.save(product);

        return ResponseEntity.ok(newProduct);
    }
}
