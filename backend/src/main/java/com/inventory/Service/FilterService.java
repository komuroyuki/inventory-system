package com.inventory.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.inventory.Repository.ProductRepository;
import com.inventory.Repository.CategoryRepository;

import com.inventory.DTO.ProductFilterResponse;
import com.inventory.Entity.Product;

@Service
public class FilterService {

        private final ProductRepository productRepository;

        private final CategoryRepository categoryRepository;

        public FilterService(
                        ProductRepository productRepository,
                        CategoryRepository categoryRepository) {

                this.productRepository = productRepository;
                this.categoryRepository = categoryRepository;
        }

        public List<ProductFilterResponse> getDetails(
                        String categoryId) {

                // 不正値チェック
                if (!categoryId.matches("\\d+")) {

                        throw new IllegalArgumentException(
                                        "E500003");
                }

                int id = Integer.parseInt(categoryId);

                // 桁数チェック
                if (id > 9999) {

                        throw new IllegalArgumentException(
                                        "E500004");
                }

                // 全取得
                List<Product> products = productRepository.findAll();

                List<Product> filteredProducts;

                if (id == 0) {

                        filteredProducts = products;

                } else {

                        // 存在チェック
                        categoryRepository.findById(id)
                                        .orElseThrow(() -> new RuntimeException(
                                                        "E500002"));

                        // カテゴリ絞り込み
                        filteredProducts = products.stream()
                                        .filter(product -> product.getCategoryId()
                                                        .getId() == id)
                                        .collect(Collectors.toList());
                }

                // 更新日降順ソート
                filteredProducts = filteredProducts.stream()
                                .sorted(
                                                Comparator.comparing(
                                                                Product::getLastModifiedDate,
                                                                Comparator.nullsLast(
                                                                                Comparator.naturalOrder()))
                                                                .reversed())
                                .collect(Collectors.toList());

                // DTO変換
                return filteredProducts.stream()
                                .map(product -> {

                                        ProductFilterResponse productResponse = new ProductFilterResponse();

                                        productResponse.setProductId(
                                                        product.getId());

                                        productResponse.setProductName(
                                                        product.getName());

                                        productResponse.setProductQuantity(
                                                        product.getQuantity());

                                        productResponse.setCategoryId(
                                                        product.getCategoryId()
                                                                        .getId());

                                        if (product.getLastModifiedDate() != null) {

                                                productResponse.setLastModifiedDate(
                                                                product.getLastModifiedDate()
                                                                                .toString());

                                        } else {

                                                productResponse.setLastModifiedDate("");
                                        }

                                        return productResponse;
                                })
                                .collect(Collectors.toList());
        }
}