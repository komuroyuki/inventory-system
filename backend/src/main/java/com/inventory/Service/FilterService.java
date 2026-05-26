package com.inventory.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.inventory.DTO.ProductFilterResponse;
import com.inventory.Entity.Product;
import com.inventory.Repository.CategoryRepository;
import com.inventory.Repository.FilterRepository;
import com.inventory.Repository.ProductRepository;

@Service
public class FilterService {

        private final ProductRepository productRepository;

        private final CategoryRepository categoryRepository;

        private final FilterRepository filterRepository;

        public FilterService(
                        ProductRepository productRepository,
                        CategoryRepository categoryRepository,
                        FilterRepository filterRepository) {

                this.productRepository = productRepository;
                this.categoryRepository = categoryRepository;
                this.filterRepository = filterRepository;
        }

        public List<ProductFilterResponse> getDetails(
                        Integer id) {

                // 桁数チェック
                if (id > 9999 && id >= 0) {

                        throw new IllegalArgumentException(
                                        "E500004 : categoryId too large");
                }

                List<Product> filteredProducts;

                // 0なら全件取得
                if (id == 0) {

                        filteredProducts = productRepository.findAll();

                } else {

                        // カテゴリ存在確認
                        categoryRepository.findById(id)
                                        .orElseThrow(() -> new RuntimeException(
                                                        "E500002 : category not found"));

                        // DB側で絞り込み
                        filteredProducts = filterRepository
                                        .findByCategoryId_Id(id);
                }

                // 更新日降順ソート
                filteredProducts = filteredProducts.stream()
                                .sorted(
                                                Comparator.comparing(
                                                                Product::getLastModifiedDate,
                                                                Comparator.nullsLast(
                                                                                Comparator.reverseOrder())))
                                .collect(Collectors.toList());

                // DTO変換
                return filteredProducts.stream()
                                .map(this::convertToResponse)
                                .collect(Collectors.toList());
        }

        // DTO変換メソッド
        private ProductFilterResponse convertToResponse(
                        Product product) {

                ProductFilterResponse response = new ProductFilterResponse();

                response.setProductId(
                                product.getId());

                response.setProductName(
                                product.getName());

                response.setProductQuantity(
                                product.getQuantity());

                response.setCategoryId(
                                product.getCategoryId().getId());

                if (product.getLastModifiedDate() != null) {

                        response.setLastModifiedDate(
                                        product.getLastModifiedDate()
                                                        .toString());

                } else {

                        response.setLastModifiedDate("");
                }

                return response;
        }
}