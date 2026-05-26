package com.inventory.Service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.inventory.Repository.ProductRepository;
import com.inventory.Repository.DetailsRepository;
import com.inventory.DTO.ProductDetailsResponse;

@Service
public class DetailsService {

    private final ProductRepository productRepository;
    private final DetailsRepository detailsRepository;

    public DetailsService(
            ProductRepository productRepository,
            DetailsRepository detailsRepository) {
        this.productRepository = productRepository;
        this.detailsRepository = detailsRepository;
    }

    public ResponseEntity<?> getDetails(Integer id) {

        // 存在チェック（.orElse(null) に変更し、データがなくても500エラーで落とさないようにします）
        com.inventory.Entity.Product product = productRepository
                .findById(id)
                .orElse(null);

        ProductDetailsResponse response = new ProductDetailsResponse();

        // 【安全策】データベースに該当の商品がない場合の処理
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("IDが存在しません。" + System.lineSeparator());
        }

        // 【正常系】データがある場合はDTOへ詰め替え
        response.setProductId(product.getId());
        response.setProductName(product.getName());
        response.setProductQuantity(product.getQuantity());
        response.setProductImageUrl(product.getImage());
        response.setCategoryId(product.getCategoryId().getId());
        // response.setNextProductId(product.getId() + 1);

        com.inventory.Entity.Product nextProduct = detailsRepository
                .findFirstByIdGreaterThanOrderByIdAsc(product.getId());
        if (nextProduct != null) {
            response.setNextProductId(nextProduct.getId());
        } else {
            response.setNextProductId(null); // 次の商品がない場合はnullをセット
        }

        if (product.getLastModifiedDate() != null) {
            response.setProductUpdatedAt(product.getLastModifiedDate().toString());
        } else {
            response.setProductUpdatedAt("");
        }

        return ResponseEntity.ok(response);
    }
}
