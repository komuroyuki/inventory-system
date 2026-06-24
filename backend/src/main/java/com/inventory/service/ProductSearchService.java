package com.inventory.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.inventory.entity.Product;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.ProductSearchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductSearchService {
    private final ProductRepository productRepository;
    private final ProductSearchRepository filterRepository;

    public List<Product> filterAndSearch(Integer categoryId, String trimmedKeyword) {

        // 検索キーワードが空の場合の処理
        if (trimmedKeyword.isEmpty()) {
            // カテゴリーIDが指定されていない場合は全ての商品を返し、指定されている場合はそのカテゴリーの商品を返す
            if (categoryId == null || categoryId == 0) {
                return productRepository.findAll();
                // カテゴリーIDが指定されている場合はそのカテゴリーの商品を返す
            } else {
                return filterRepository.findByCategoryIdId(categoryId);
            }
        }

        // 検索キーワードが空でない場合の処理
        if (categoryId == null || categoryId == 0) {
            return productRepository.findByNameContaining(trimmedKeyword);
            // カテゴリーIDが指定されている場合はそのカテゴリーの商品から検索キーワードを含む商品を返す
        } else {
            return filterRepository.findByCategoryIdIdAndNameContaining(categoryId, trimmedKeyword);
        }
    }

}
