package com.inventory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.service.DetailsService;

@RestController

@CrossOrigin(origins = "*")

public class ProductDetailController {

    private final DetailsService detailsService;

    public ProductDetailController(
            DetailsService detailsService) {

        this.detailsService = detailsService;
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> productDetailsResponse(@PathVariable Integer id) {

        return detailsService.getDetails(id);
    }
}
