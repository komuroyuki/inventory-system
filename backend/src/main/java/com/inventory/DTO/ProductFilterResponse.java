package com.inventory.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductFilterResponse {

    private Integer productId;

    private String productName;

    private Integer productQuantity;

    private String lastModifiedDate;

    private Integer categoryId;

}