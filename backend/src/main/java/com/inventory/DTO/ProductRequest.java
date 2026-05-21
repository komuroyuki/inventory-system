package com.inventory.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequest(
        Integer id,
        @NotNull String name,
        @NotNull @Min(0) Integer quantity,
        @NotBlank String image,
        @NotNull Integer categoryId) {
}
