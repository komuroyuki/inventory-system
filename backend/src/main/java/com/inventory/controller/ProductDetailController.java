package com.inventory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.service.ProductDetailService;

@RestController

@CrossOrigin(origins = "*")

public class ProductDetailController {

    private final ProductDetailService service;

    public ProductDetailController(
            ProductDetailService service) {

        this.service = service;
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> productDetailsResponse(@PathVariable Integer id) {

        return service.getDetails(id);
    }

}
