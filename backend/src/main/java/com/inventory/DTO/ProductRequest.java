package com.inventory.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequest(
        Integer id,
        @NotBlank String name,
        @NotNull @Min(0) Integer quantity,
        String image,
        @NotNull Integer categoryId) {
}
