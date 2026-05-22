package com.inventory.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.DTO.ProductRequest;
import com.inventory.Service.ProductReplaceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductReplaceController {
    private final ProductReplaceService productReplaceService;

    @CrossOrigin("http://localhost:5173")
    @PutMapping("/{id}")
    public ResponseEntity<?> replaceProduct(@Validated @RequestBody ProductRequest newProduct,
            @PathVariable Integer id) {
        return productReplaceService.replaceProduct(newProduct, id);
    }
}
