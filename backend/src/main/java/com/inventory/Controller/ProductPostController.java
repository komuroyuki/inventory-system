package com.inventory.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.DTO.ProductRequest;
import com.inventory.Service.ProductPostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductPostController {

    private final ProductPostService service;

    @PostMapping("/products")
    public ResponseEntity<?> postProduct(@RequestBody @Validated ProductRequest request) {

        return service.postProduct(request);
    }
}
