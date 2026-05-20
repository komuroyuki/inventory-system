package com.inventory.Service;

import org.springframework.stereotype.Service;

import com.inventory.Repository.ProductRepository;
import com.inventory.DTO.ProductDetailsResponse;

@Service
public class DetailsService {

    private final ProductRepository productRepository;

    public DetailsService(
            ProductRepository productRepository) {

        this.productRepository = productRepository;
    }

    public ProductDetailsResponse getDetails(
            String productId) {

        // 不正値チェック
        if (!productId.matches("\\d+")) {

            throw new IllegalArgumentException(
                    "E500003");
        }

        int id = Integer.parseInt(productId);

        // 桁数チェック
        if (id > 9999) {

            throw new IllegalArgumentException(
                    "E500004");
        }

        // 存在チェック
        com.inventory.Entity.Product product = productRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "E500002"));

        ProductDetailsResponse response =
                new ProductDetailsResponse();

        response.setProductId(
                product.getId());

        response.setProductName(
                product.getName());

        response.setProductQuantity(
                product.getQuantity());

        response.setProductImageUrl(
                product.getImage());

        if (product.getLastModifiedDate() != null) {
            response.setProductUpdatedAt(product.getLastModifiedDate().toString());
        } else {
            response.setProductUpdatedAt("");
        }

        response.setCategoryId(
                product.getCategoryId());

        return response;
    }





    
}