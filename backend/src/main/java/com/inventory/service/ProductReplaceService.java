package com.inventory.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.inventory.dto.ProductRequest;
import com.inventory.entity.Category;
import com.inventory.entity.Product;
import com.inventory.repository.CategoryRepository;
import com.inventory.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductReplaceService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ResponseEntity<?> replaceProduct(ProductRequest newProduct, Integer id) {
        Category category = categoryRepository.findById(newProduct.categoryId()).orElse(null);

        if (category == null) {
            return ResponseEntity.badRequest().body("カテゴリIDが存在しません。" + System.lineSeparator());
        }

        Product updatedProduct = productRepository.findById(id).map(product -> {
            product.setName(newProduct.name());
            product.setQuantity(newProduct.quantity());
            product.setImage(newProduct.image());
            product.setCategoryId(category);

            return productRepository.save(product);
        }).orElse(null);

        if (updatedProduct == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("IDが存在しません。" + System.lineSeparator());
        }

        return ResponseEntity.ok(updatedProduct);
    }
}
