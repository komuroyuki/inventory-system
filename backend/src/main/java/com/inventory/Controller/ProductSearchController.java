package com.inventory.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.Entity.Product;
import com.inventory.Service.ProductSearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    @GetMapping("/product/search")
    public List<Product> searchProduct(@RequestParam String keyword) {
        return productSearchService.searchProduct(keyword);
    }
}
