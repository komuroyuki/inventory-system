package com.inventory.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.inventory.dto.ProductRequest;
import com.inventory.dto.ProductResponse;
import com.inventory.entity.Category;
import com.inventory.entity.Product;
import com.inventory.repository.CategoryRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductPostService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductResponse postProduct(ProductRequest request) {

        String name = request.name().strip();
        List<Product> existingProducts = productRepository.findByName(name);

        if (!existingProducts.isEmpty()) {
            throw new BadRequestException("商品が存在します。");
        }

        Category category = categoryRepository.findById(request.categoryId()).orElse(null);

        if (category == null) {
            throw new BadRequestException("カテゴリIDが存在しません。");
        }

        Product product = new Product();

        product.setName(name);
        product.setQuantity(request.quantity());
        product.setImage(request.image());
        product.setCategoryId(category);

        Product newProduct = productRepository.save(product);
        ProductResponse response = new ProductResponse(
                newProduct.getId(),
                newProduct.getName(),
                newProduct.getQuantity(),
                newProduct.getImage(),
                newProduct.getCategoryId().getId());

        return response;
    }
}
