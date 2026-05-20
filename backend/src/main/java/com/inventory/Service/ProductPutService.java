package com.inventory.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.inventory.Entity.Category;
import com.inventory.Entity.Product;
import com.inventory.Repository.CategoryRepository;
import com.inventory.Repository.ProductRepository;

@Service
public class ProductPutService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public ResponseEntity<?> replaceProduct(Product newProduct, Integer id) {
        Optional<Product> updatedProduct = productRepository.findById(id);

        Integer categoryId = newProduct.getCategoryId().getId();
        Category category = categoryRepository.findById(categoryId).orElse(null);

        if (updatedProduct == null) {
            return ResponseEntity.notFound().build();
        }

        if (category == null) {
            return ResponseEntity.badRequest().build();
        }

        updatedProduct.map(product -> {
            product.setName(newProduct.getName());
            product.setQuantity(newProduct.getQuantity());
            product.setImage(newProduct.getImage());
            product.setCategoryId(category);

            return productRepository.save(product);
        });

        return ResponseEntity.ok(updatedProduct);
    }
}
