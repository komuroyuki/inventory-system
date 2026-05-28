package com.inventory.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.inventory.Entity.Product;
import java.util.List;

import com.inventory.Service.FilterAndSearchService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequiredArgsConstructor
public class FilterAndSearchController {

    private final FilterAndSearchService filterAndSearchService;

    @GetMapping("/products/search-filter")
    public ResponseEntity<?> getProducts(
            @RequestParam(value = "category_id", required = false) Integer categoryId,
            @RequestParam(value = "keyword", required = false) String keyword) {

        // 全角スペースを半角スペースに変換し、前後のスペースをトリム
        String trimmedKeyword = (keyword == null) ? "" : keyword.replace("　", " ").trim();
        
        // 検索キーワードが空でない場合のバリデーション
        if (!trimmedKeyword.isEmpty()) {
            // 検索キーワードが50文字を超えていないかをチェック
            if (trimmedKeyword.length() > 50) {
                return ResponseEntity.badRequest().body("検索文字は50文字以内で入力してください。");
            }
            // 使用できない文字が含まれていないかをチェック
            if (!trimmedKeyword.matches("^[ぁ-んァ-ヶ一-龠a-zA-Z0-9ー・\\s]+$")) {
                return ResponseEntity.badRequest().body("使用できない文字が含まれています。");
            }
        }
        // フィルタリングと検索の実行
        List<Product> products = filterAndSearchService.filterAndSearch(categoryId, trimmedKeyword);
        return ResponseEntity.ok(products);
    }
}