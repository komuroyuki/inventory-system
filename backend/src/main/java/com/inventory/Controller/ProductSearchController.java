package com.inventory.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.Service.ProductSearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    @CrossOrigin("http://localhost:5173")
    @GetMapping("/product/search")
    public ResponseEntity<?> searchProduct(@RequestParam String keyword) {
        return productSearchService.searchProduct(keyword);

    }
}
