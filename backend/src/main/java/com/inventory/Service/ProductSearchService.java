package com.inventory.Service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.inventory.Entity.Product;
import com.inventory.Repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductRepository productRepository;

    public ResponseEntity<?> searchProduct(String keyword) {
        if (keyword == null) {
            return ResponseEntity.badRequest().body("検索文字を入力してください。");
        }

        String trimmedKeyword = keyword.trim();

        if (trimmedKeyword.isEmpty()) {
            return ResponseEntity.badRequest().body("検索文字を入力してください。");
        }

        if (trimmedKeyword.length() > 50) {
            return ResponseEntity.badRequest().body("検索文字は50文字以内で入力してください。");
        }

        if (!trimmedKeyword.matches("^[ぁ-んァ-ヶ一-龠a-zA-Z0-9ー・\\s]+$")) {
            return ResponseEntity.badRequest().body("使用できない文字が含まれています。");
        }

        List<Product> products = productRepository.findByNameContaining(trimmedKeyword);

        return ResponseEntity.ok(products);
    }
}
