package com.inventory.DTO;

public record ProductResponse(
        String name,
        Integer quantity,
        String image,
        Integer categoryId) {
}
