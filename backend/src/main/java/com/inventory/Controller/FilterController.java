package com.inventory.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.DTO.ProductFilterResponse;
import com.inventory.Service.FilterService;

@RestController
public class FilterController {

    private final FilterService filterService;

    public FilterController(
            FilterService filterService) {

        this.filterService = filterService;
    }

    @GetMapping("/products/category")
    public List<ProductFilterResponse> productDetailsResponse(
            @RequestParam("category_id") String categoryId) {

        return filterService.getDetails(categoryId);
    }
}