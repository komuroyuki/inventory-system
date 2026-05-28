package com.inventory.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.inventory.Entity.Product;
import com.inventory.Repository.ProductRepository;
import com.inventory.Repository.FilterRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class FilterAndSearchService {
    private final ProductRepository productRepository;
    private final FilterRepository filterRepository;

    public List<Product> filterAndSearch(Integer categoryId, String trimmedKeyword) {

        if (trimmedKeyword.isEmpty()) {
            if (categoryId == null || categoryId == 0) {
                return productRepository.findAll();
            } else {
                return filterRepository.findByCategoryIdId(categoryId);
            }
        }

        if (categoryId == null || categoryId == 0) {
            return productRepository.findByNameContaining(trimmedKeyword);
        } else {
            return filterRepository.findByCategoryIdIdAndNameContaining(categoryId, trimmedKeyword);
        }
    }
}