package com.inventory.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.DTO.ProductRequest;
import com.inventory.DTO.ProductResponse;
import com.inventory.Service.ProductPostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductPostController {

    private final ProductPostService service;

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> postProduct(@RequestBody @Valid ProductRequest request) {

        return ResponseEntity.ok(service.postProduct(request));
    }
}
