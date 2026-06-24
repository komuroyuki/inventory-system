package com.inventory.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailResponse {

    private Integer productId;

    private String productName;

    private Integer productQuantity;

    private String productImageUrl;

    private String productUpdatedAt;

    private Integer categoryId;

    private Integer nextProductId;

    private Integer prevProductId;
}
