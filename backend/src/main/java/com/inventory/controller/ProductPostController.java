package com.inventory.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.DTO.ProductRequest;
import com.inventory.DTO.ProductResponse;
import com.inventory.Service.ProductPostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ProductPostController {

    private final ProductPostService service;

    @PostMapping("/products")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<ProductResponse> postProduct(@RequestBody @Valid ProductRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(service.postProduct(request));
    }
}
