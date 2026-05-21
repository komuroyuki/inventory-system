package com.inventory.Service;

import java.util.Optional;

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
        Optional<Product> updatedProduct = productRepository.findById(id);

        Integer categoryId = newProduct.categoryId();
        Category category = categoryRepository.findById(categoryId).orElse(null);

        if (updatedProduct == null) {
            return ResponseEntity.notFound().build();
        }

        if (category == null) {
            return ResponseEntity.badRequest().build();
        }

        updatedProduct.map(product -> {
            product.setName(newProduct.name());
            product.setQuantity(newProduct.quantity());
            product.setImage(newProduct.image());
            product.setCategoryId(category);

            return productRepository.save(product);
        });

        return ResponseEntity.ok(updatedProduct);
    }
}
