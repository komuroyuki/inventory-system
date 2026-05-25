package com.inventory.DTO;

import com.inventory.Entity.Category;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailsResponse {

    private Integer productId;

    private String productName;

    private Integer productQuantity;

    private String productImageUrl;

    private String productUpdatedAt;

    private Category categoryId;

    private Integer nextProductId;
}