package com.inventory.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.Entity.Product;
import com.inventory.Service.ProductPutService;

@RestController
@RequestMapping("/products")
public class ProductReplaceController {
    @Autowired
    private ProductPutService productPutService;

    @PutMapping("/{id}")
    public ResponseEntity<?> replaceProduct(@Validated @RequestBody Product newProduct, @PathVariable Integer id) {
        return productPutService.replaceProduct(newProduct, id);
    }
}
