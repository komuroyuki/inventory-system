package com.inventory.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.DTO.ProductDetailsResponse;
import com.inventory.Service.DetailsService;

@RestController
public class DetailsController {

    private final DetailsService detailsService;

    public DetailsController(
            DetailsService detailsService) {

        this.detailsService = detailsService;
    }

    @GetMapping("/products")
    public ProductDetailsResponse productDetailsResponse(
            @RequestParam("product_id")
            String productId) {

        return detailsService.getDetails(productId);
    }
}