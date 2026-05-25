package com.inventory.Controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.DTO.ProductDetailsResponse;
import com.inventory.Service.DetailsService;

@RestController

@CrossOrigin(origins = "*")

public class DetailsController {

    private final DetailsService detailsService;

    public DetailsController(
            DetailsService detailsService) {

        this.detailsService = detailsService;
    }

    @GetMapping("/products/{id}")
    public ProductDetailsResponse productDetailsResponse(@PathVariable Integer id) {

        return detailsService.getDetails(id);
    }
}
