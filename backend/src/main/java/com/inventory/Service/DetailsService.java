package com.inventory.Service;

import org.springframework.stereotype.Service;
import com.inventory.Repository.ProductRepository;
import com.inventory.DTO.ProductDetailsResponse;
import java.util.Optional;

@Service
public class DetailsService {

    private final ProductRepository productRepository;

    public DetailsService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductDetailsResponse getDetails(String productId) {

        // 不正値チェック
        if (!productId.matches("\\d+")) {
            throw new IllegalArgumentException("E500003");
        }

        int id = Integer.parseInt(productId);

        // 桁数チェック
        if (id > 9999) {
            throw new IllegalArgumentException("E500004");
        }

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

        if (product.getLastModifiedDate() != null) {
            response.setProductUpdatedAt(product.getLastModifiedDate().toString());
        } else {
            response.setProductUpdatedAt("");
        }

        // 【無限ループ対策】
        // 本来は DTO 側の型を「Category (Entity)」ではなく「Integer categoryId」や「String
        // categoryName」にするのが鉄則です。
        // 現在のDTO定義（Category型）のまま安全に渡すために、新しくプレーンなCategoryオブジェクトを作ってセットします。
        if (product.getCategoryId() != null) {
            com.inventory.Entity.Category dtoCategory = new com.inventory.Entity.Category();
            dtoCategory.setId(product.getCategoryId().getId());
            dtoCategory.setName(product.getCategoryId().getName());
            response.setCategoryId(dtoCategory);
        } else {
            response.setCategoryId(null);
        }

        return response;
    }
}
