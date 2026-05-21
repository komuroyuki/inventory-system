package com.inventory.Controller;

import com.inventory.Entity.Product;
import com.inventory.Service.ProductListService;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class ProductListController {

    private final ProductListService productListService;

    public ProductListController(ProductListService productListService) {
        this.productListService = productListService;
    }
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/product")
        public List<Product> getProductList() {
             return productListService.getProductList();
}
}