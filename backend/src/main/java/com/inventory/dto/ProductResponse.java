package com.inventory.dto;

public record ProductResponse(
        Integer id,
        String name,
        Integer quantity,
        String image,
        Integer categoryId) {
}
