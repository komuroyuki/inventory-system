package com.inventory.Controller;

import com.inventory.Entity.Product;
import com.inventory.Service.ProductListService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ProductListController {

    private final ProductListService productListService;

    public ProductListController(ProductListService productListService) {
        this.productListService = productListService;
    }

    @GetMapping("/product")
        public Iterable<Product> getProductList() {
             return productListService.getProductList();
}
}