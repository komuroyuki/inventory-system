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
public class ProductReplaceService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ResponseEntity<?> replaceProduct(ProductRequest newProduct, Integer id) {
        Category category = categoryRepository.findById(newProduct.categoryId()).orElse(null);

        if (category == null) {
            return ResponseEntity.badRequest().build();
        }

        Product updatedProduct = productRepository.findById(id).map(product -> {
            product.setName(newProduct.name());
            product.setQuantity(newProduct.quantity());
            product.setImage(newProduct.image());
            product.setCategoryId(category);

            return productRepository.save(product);
        }).orElse(null);

        if (updatedProduct == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedProduct);
    }
}
