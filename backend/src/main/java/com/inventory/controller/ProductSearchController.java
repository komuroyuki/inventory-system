package com.inventory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.inventory.entity.Product;
import java.util.List;

import com.inventory.service.ProductSearchService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService service;

    @GetMapping("/products/search-filter")
    public ResponseEntity<?> getProducts(
            @RequestParam(value = "category_id", required = false) Integer categoryId,
            @RequestParam(value = "keyword", required = false) String keyword) {

        // 全角スペースを半角スペースに変換し、前後のスペースをトリム
        String trimmedKeyword = (keyword == null) ? "" : keyword.replace("　", " ").trim();

        //全角→半角変換
        String normalized = java.text.Normalizer.normalize(
            trimmedKeyword,
            java.text.Normalizer.Form.NFKC
        );

        // 検索キーワードが空でない場合のバリデーション
        if (!normalized.isEmpty()) {
            // 検索キーワードが50文字を超えていないかをチェック
            if (normalized.length() > 50) {
                return ResponseEntity.badRequest().body("正しく入力してください");
            }
            // 使用できない文字が含まれていないかをチェック
            if (!normalized.matches("^[ぁ-んァ-ヶ一-龠a-zA-Z0-9ー・\\s]+$")) {
                return ResponseEntity.badRequest().body("正しく入力してください");
            }
        }
        // フィルタリングと検索の実行
        List<Product> products = service.filterAndSearch(categoryId, normalized);
        return ResponseEntity.ok(products);
    }

}
