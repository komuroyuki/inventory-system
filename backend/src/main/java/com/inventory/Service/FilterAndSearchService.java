package com.inventory.Service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.inventory.Entity.Product;
import com.inventory.Repository.ProductRepository;
import com.inventory.Repository.FilterRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class FilterAndSearchService {
    private final ProductRepository productRepository;
    private final FilterRepository filterRepository;

    // 文字列検索のみ
    public ResponseEntity<?> searchProduct(String keyword) {
        if (keyword == null) {
            return ResponseEntity.badRequest().body("検索文字を入力してください。");
        }
        // 全角スペースを半角スペースに変換し、前後のスペースを削除
        String trimmedKeyword = keyword
                .replace("　", " ")
                .trim();
        
        // 入力がされていない場合は全件表示
        if (trimmedKeyword.isEmpty()) {
            return ResponseEntity.ok(productRepository.findAll());
        }
        // 入力が50文字を超える場合はエラー
        if (trimmedKeyword.length() > 50) {
            return ResponseEntity.badRequest().body("検索文字は50文字以内で入力してください。");
        }
        // 指定の文字以外が含まれている場合はエラー
        if (!trimmedKeyword.matches("^[ぁ-んァ-ヶ一-龠a-zA-Z0-9ー・\\s]+$")) {
            return ResponseEntity.badRequest().body("使用できない文字が含まれています。");
        }
        // 検索処理
        List<Product> products = productRepository.findByNameContaining(trimmedKeyword);
        return ResponseEntity.ok(products);
    }

    // カテゴリ絞り込みと文字列検索の両方
    public ResponseEntity<?> filterAndSearch(Integer categoryId, String keyword) {

        // keywordがnull、または全角半角スペース置換後に空文字になる場合
        String trimmedKeyword = "";
        if (keyword != null) {
            trimmedKeyword = keyword.replace("　", " ").trim();
        }

        // 検索ワードがないor全角半角スペースのみの場合
        if (keyword == null || trimmedKeyword.isEmpty()) {
            // 全件表示（カテゴリなし）
            if (categoryId == null || categoryId == 0) {
                return ResponseEntity.ok(productRepository.findAll());
            // カテゴリ絞り込み（カテゴリあり）
            } else {
                return ResponseEntity.ok(filterRepository.findByCategoryIdId(categoryId));
            }
        }

        // 入力が50文字を超える場合はエラー
        if (trimmedKeyword.length() > 50) {
            return ResponseEntity.badRequest().body("検索文字は50文字以内で入力してください。");
        }

        // 指定の文字以外が含まれている場合はエラー
        if (!trimmedKeyword.matches("^[ぁ-んァ-ヶ一-龠a-zA-Z0-9ー・\\s]+$")) {
            return ResponseEntity.badRequest().body("使用できない文字が含まれています。");
        }

        // カテゴリ絞り込み&検索処理
        List<Product> products;
        if (categoryId == null || categoryId == 0) {
            products = productRepository.findByNameContaining(trimmedKeyword);
        } else {
            products = filterRepository.findByCategoryIdIdAndNameContaining(categoryId, trimmedKeyword);
        }

        return ResponseEntity.ok(products);
    }
}
