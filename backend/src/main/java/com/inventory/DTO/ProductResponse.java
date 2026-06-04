package com.inventory.DTO;

public record ProductResponse(
        Integer id,
        String name,
        Integer quantity,
        String image,
        Integer categoryId) {
}
