package com.inventory.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.Service.FilterAndSearchService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173") // フロントエンドのURLに合わせてください
@RestController
@RequiredArgsConstructor
public class FilterAndSearchController {

    private final FilterAndSearchService filterAndSearchService;

    @GetMapping("/products/search-filter")
    public ResponseEntity<?> getProducts(
            @RequestParam(value = "category_id", required = false) Integer categoryId,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return filterAndSearchService.filterAndSearch(categoryId, keyword);
    }
}
