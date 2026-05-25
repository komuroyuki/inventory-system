package com.inventory.Service;

import org.springframework.stereotype.Service;
import com.inventory.Repository.ProductRepository;
import com.inventory.DTO.ProductDetailsResponse;

@Service
public class DetailsService {

    private final ProductRepository productRepository;

    public DetailsService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductDetailsResponse getDetails(Integer id) {

        // 存在チェック（.orElse(null) に変更し、データがなくても500エラーで落とさないようにします）
        com.inventory.Entity.Product product = productRepository
                .findById(id)
                .orElse(null);

        ProductDetailsResponse response = new ProductDetailsResponse();

        // 【安全策】データベースに該当の商品がない場合の処理
        if (product == null) {
            response.setProductId(id);
            response.setProductName("未登録の商品（ID: " + id + "）");
            response.setProductQuantity(0);
            response.setProductImageUrl("");
            response.setProductUpdatedAt("");
            response.setCategoryId(null); // データがないのでnull
            return response;
        }

        // 【正常系】データがある場合はDTOへ詰め替え
        response.setProductId(product.getId());
        response.setProductName(product.getName());
        response.setProductQuantity(product.getQuantity());
        response.setProductImageUrl(product.getImage());
        response.setCategoryId(product.getCategoryId().getId());

        if (product.getLastModifiedDate() != null) {
            response.setProductUpdatedAt(product.getLastModifiedDate().toString());
        } else {
            response.setProductUpdatedAt("");
        }

        return response;
    }
}
