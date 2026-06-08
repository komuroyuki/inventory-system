package com.inventory.Service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.inventory.DTO.ProductDetailsResponse;
import com.inventory.Entity.Product;
import com.inventory.Repository.DetailsRepository;
import com.inventory.Repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DetailsService {

    private final ProductRepository productRepository;
    private final DetailsRepository detailsRepository;

    public ResponseEntity<?> getDetails(Integer id) {

        if (id == null) {
            return ResponseEntity
                    .badRequest()
                    .body("IDを入力してください。");
        }

        Product product = productRepository
                .findById(id)
                .orElse(null);

        if (product == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("IDが存在しません。");
        }

        ProductDetailsResponse response =
                new ProductDetailsResponse();

        response.setProductId(product.getId());
        response.setProductName(product.getName());
        response.setProductQuantity(product.getQuantity());
        response.setProductImageUrl(product.getImage());
        response.setCategoryId(product.getCategoryId().getId());

        Product nextProduct = detailsRepository
                .findFirstByIdGreaterThanOrderByIdAsc(product.getId());

        response.setNextProductId(
                nextProduct != null
                        ? nextProduct.getId()
                        : null);

        response.setProductUpdatedAt(
                product.getLastModifiedDate() != null
                        ? product.getLastModifiedDate().toString()
                        : "");

        Product prevProduct = detailsRepository
                .findFirstByIdLessThanOrderByIdDesc(product.getId());

        response.setPrevProductId(
                prevProduct != null
                        ? prevProduct.getId()
                        : null);

        return ResponseEntity.ok(response);
    }
}